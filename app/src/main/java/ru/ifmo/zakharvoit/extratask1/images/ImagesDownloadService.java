package ru.ifmo.zakharvoit.extratask1.images;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;

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
    public static final String RESULT_RECEIVER = "ru.ifmo.zakharvoit.extratask1.result_receiver";
    public static final String IMAGES_LIST_URL = "http://api-fotki.yandex.ru/api/podhistory/?format=json";
    public static final String PREVIEW_SIZE = "XXS";

    public ImagesDownloadService() {
        super("ru.ifmo.zakharvoit.extratask1.ImagesDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        try {
            String[] uriList = fetchUriList();
            Bundle bundle = new Bundle();
            bundle.putInt(ImagesResultReceiver.SIZE_BUNDLE_KEY, uriList.length);
            receiver.send(ImagesResultReceiver.LIST_DOWNLOADED, bundle);
            bundle.clear();
            for (String uri : uriList) {
                InputStream is = new URL(uri).openStream();
                Bitmap bitmap = BitmapFactory
                        .decodeStream(is);
                is.close();
                bundle.putParcelable(ImagesResultReceiver.IMAGE_BUNDLE_KEY, bitmap);
                receiver.send(ImagesResultReceiver.IMAGE_DOWNLOADED, bundle);
                bundle.clear();
            }
            receiver.send(ImagesResultReceiver.FINISHED, Bundle.EMPTY);
        } catch (Exception e) {
            receiver.send(ImagesResultReceiver.ERROR, Bundle.EMPTY);
        }
    }

    public String[] fetchUriList() throws IOException, JSONException {
        URL url = new URL(IMAGES_LIST_URL);
        String contents = StreamUtil.inputStreamToString(url.openStream());
        JSONObject root = new JSONObject(contents);
        JSONArray images = root.getJSONArray("entries");

        String[] result = new String[images.length()];
        for (int i = 0; i < images.length(); i++) {
            JSONObject image = images.getJSONObject(i);
            result[i] = image.getJSONObject(PREVIEW_SIZE)
                               .getString("href");
        }

        return result;
    }
}
