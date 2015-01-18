package ru.ifmo.zakharvoit.extratask1.images;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ru.ifmo.zakharvoit.extratask1.util.StreamUtil;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class ImagesDownloadService extends IntentService {
    public static final String RESULT_RECEIVER_EXTRA_KEY = "ru.ifmo.zakharvoit.extratask1.result_receiver";
    public static final String IMAGES_LIST_URL = "http://api-fotki.yandex.ru/api/podhistory/?format=json";
    public static final String SMALL_SIZE = "M";
    public static final String LARGE_SIZE = "XXL";
    public static final String TAG = "ImagesDownloaderService";

    public ImagesDownloadService() {
        super("ru.ifmo.zakharvoit.extratask1.ImagesDownloadService");
    }

    private static class ImageData {
        private String title;
        private String smallLink;
        private String largeLink;

        private ImageData() {
        }

        public String getTitle() {
            return title;
        }

        public String getSmallLink() {
            return smallLink;
        }

        public String getLargeLink() {
            return largeLink;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSmallLink(String smallLink) {
            this.smallLink = smallLink;
        }

        public void setLargeLink(String largeLink) {
            this.largeLink = largeLink;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_EXTRA_KEY);

        try {
            ImageData[] imagesList = fetchUriList();
            Bundle listSizeBundle = new Bundle();
            listSizeBundle.putInt(ImagesResultReceiver.SIZE_BUNDLE_KEY, imagesList.length);
            receiver.send(ImagesResultReceiver.LIST_DOWNLOADED, listSizeBundle);
            for (ImageData image : imagesList) {
                String title = image.getTitle();
                String smallLink = image.getSmallLink();
                String largeLink = image.getLargeLink();

                InputStream is = new URL(smallLink).openStream();
                byte[] value = StreamUtil.inputStreamToByteArray(is);

                Image newImage = new Image(title, value, largeLink);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ImagesResultReceiver.IMAGE_BUNDLE_KEY,
                        newImage);
                receiver.send(ImagesResultReceiver.IMAGE_DOWNLOADED, bundle);
            }
            receiver.send(ImagesResultReceiver.FINISHED, Bundle.EMPTY);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            receiver.send(ImagesResultReceiver.ERROR, Bundle.EMPTY);
        }
    }

    public ImageData[] fetchUriList() throws IOException, JSONException {
        URL url = new URL(IMAGES_LIST_URL);
        String contents = StreamUtil.inputStreamToString(url.openStream());
        JSONObject root = new JSONObject(contents);
        JSONArray images = root.getJSONArray("entries");

        ImageData[] result = new ImageData[images.length()];
        for (int i = 0; i < images.length(); i++) {
            JSONObject entry = images.getJSONObject(i);
            JSONObject image = entry.getJSONObject("img");
            result[i] = new ImageData();

            result[i].setTitle(entry.getString("title"));
            result[i].setSmallLink(image.getJSONObject(SMALL_SIZE)
                    .getString("href"));
            result[i].setLargeLink(image.getJSONObject(LARGE_SIZE)
                    .getString("href"));
        }

        return result;
    }
}
