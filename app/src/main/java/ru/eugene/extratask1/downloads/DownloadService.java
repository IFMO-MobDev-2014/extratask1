package ru.eugene.extratask1.downloads;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.eugene.extratask1.db.ImageItem;

/**
 * Created by eugene on 1/17/15.
 */
public class DownloadService extends IntentService {
    public static final String NOTIFICATION = "ru.eugene.extratask1.downloads";
    public static final String URL = "http://api-fotki.yandex.ru/api/top/?format=json";
    public static final String RESULT = "result";
    public static Boolean READY = false;

    public DownloadService() {
        super("DownloadService");
    }

    public List readJsonStream(InputStream in) throws IOException, JSONException {
        ArrayList<ImageItem> result = new ArrayList<>();
        String text = convertStreamToString(in);
        JSONObject reader = new JSONObject(text);
        JSONArray images = reader.getJSONArray("entries");
        for (int i = 0; i < images.length(); i++) {
            ImageItem item = new ImageItem();
            JSONObject curImage = images.getJSONObject(i);
            String id = curImage.getString("id");
            item.setImageId(id);
            JSONObject diffImages = curImage.getJSONObject("img");
            item.setThumbnailUrl(diffImages.getJSONObject("S").getString("href"));
            item.setBigImageUrl(diffImages.getJSONObject("L").getString("href"));
            result.add(item);
        }

        return result;
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        READY = false;
        Intent result = new Intent(NOTIFICATION);
        try {
            InputStream is = downloadUrl(URL);
            ArrayList<ImageItem> items = (ArrayList<ImageItem>) readJsonStream(is);
            Log.e("LOG", "onHandleINtent.items.size(): " + items.size());
            result.putExtra(RESULT, items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        READY = true;

        sendBroadcast(result);
    }

    private InputStream downloadUrl(String myurl) throws IOException {

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(1000);
        conn.setConnectTimeout(1500);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

}
