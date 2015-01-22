package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
    public static final String EXTRA_PROGRESS = "extra_percents";
    public static final String EXTRA_LARGE = "extra_large";
    public static final String EXTRA_ID = "extra_id";

    private static final String YANDEX_API =
            "http://api-fotki.yandex.ru/api/podhistory/?format=json";

    public static final String BROADCAST_ACTION =
            "ru.ifmo.md.extratask1.BROADCAST";
    public static final String BROADCAST_LARGE =
            "ru.ifmo.md.extratask1.BROADCAST_LARGE";

    public static final String ACTION_ALL = "ru.ifmo.md.extratask1.ACTION_ALL";
    public static final String ACTION_LARGE = "ru.ifmo.md.extratask1.ACTION_LARGE";


    private Handler handler = new Handler();

    public ImageIntentService() {
        super("ImageIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                String action = intent.getAction();
                if (ACTION_ALL.equals(action)) {
                    downloadAll();
                } else if (ACTION_LARGE.equals(action)) {
                    byte[] bytes = getImage(intent.getStringExtra(EXTRA_LARGE));
                    long id = intent.getLongExtra(EXTRA_ID, 1);
                    Uri uri = ContentUris.withAppendedId(Tables.Images.CONTENT_URI, id);
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    ContentValues cv = new ContentValues();
                    cv.put(Tables.Images.LARGE_NAME, bytes);
                    getContentResolver().update(uri, cv, null, null);
                    Intent localIntent =
                            new Intent(BROADCAST_LARGE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
            }
        } catch (Exception e) {
            showToast(getString(R.string.error_message));
            e.printStackTrace();
        }
    }

    private void downloadAll() throws IOException, JSONException{
        JSONArray images = load(YANDEX_API).getJSONArray("entries");

        getContentResolver().delete(Tables.Images.CONTENT_URI, null, null);

        int l = images.length();
        for (int i = 0; i < l; i++) {
            JSONObject image = images.getJSONObject(i).getJSONObject("img");

            Log.i("JSON:", image.toString());
            String smallURL = image.getJSONObject("M").getString("href");
            String largeURL = image.getJSONObject("L").getString("href");
            String origURL;
            if (!image.isNull("orig")) {
                origURL = image.getJSONObject("orig").getString("href");
            } else {
                origURL = image.getJSONObject("XXXL").getString("href");
            }

            ContentValues cv = new ContentValues();
            cv.put(Tables.Images.SMALL_NAME, getImage(smallURL));
            cv.put(Tables.Images.LARGE_URL_NAME, largeURL);
            cv.put(Tables.Images.ORIG_URL_NAME, origURL);
            getContentResolver().insert(Tables.Images.CONTENT_URI, cv);

            Intent localIntent =
                    new Intent(BROADCAST_ACTION)
                            .putExtra(EXTRA_PROGRESS, 100 * (i + 1) / l);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
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
