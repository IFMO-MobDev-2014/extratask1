package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageIntentService extends IntentService {
    public static final String EXTRA_PERCENTS = "extra_percents";

    private static final String YANDEX_API =
            "http://api-fotki.yandex.ru/api/podhistory/?format=json";

    public static final String BROADCAST_ACTION =
            "ru.ifmo.md.extratask1.BROADCAST";

    private Handler handler = new Handler();

    public ImageIntentService() {
        super("ImageIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            JSONArray images = load(YANDEX_API).getJSONArray("entries");

            getContentResolver().delete(Image.JustImage.CONTENT_URI, null, null);

            int l = images.length();
            for (int i = 0; i < l; i++) {
                JSONObject image = images.getJSONObject(i).getJSONObject("img");

                String smallURL = image.getJSONObject("M").getString("href");
                String largeURL = image.getJSONObject("L").getString("href");

                ContentValues cv = new ContentValues();
                cv.put(Image.JustImage.SMALL_NAME, getImage(smallURL));
                cv.put(Image.JustImage.LARGE_NAME, largeURL);
                getContentResolver().insert(Image.JustImage.CONTENT_URI, cv);

                Intent localIntent =
                        new Intent(BROADCAST_ACTION)
                                .putExtra(EXTRA_PERCENTS, 100 * (i + 1) / l);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        } catch (Exception e) {
            showToast(getString(R.string.error_message));
            e.printStackTrace();
        }
    }

    private byte[] getImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        HttpURLConnection ucon =
                (HttpURLConnection) imageUrl.openConnection();

        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);

        ByteArrayBuffer baf = new ByteArrayBuffer(500);
        int current = 0;
        while ((current = bis.read()) != -1) {
            baf.append((byte) current);
        }

        return baf.toByteArray();
    }

    private JSONObject load(String from) throws IOException, JSONException {
        URL url = new URL(from);
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        StringBuilder json = new StringBuilder(1024);
        String tmp;
        while ((tmp = reader.readLine()) != null)
            json.append(tmp).append("\n");
        reader.close();

        return new JSONObject(json.toString());
    }

    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ImageIntentService.this,
                        text,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
