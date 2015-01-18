package com.example.vlad107.extratask1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {
    public static final String SERVICE_NAME = "downloadService";
    public static final String ACTION_RESPONSE = "com.example.vlad107.extratask1.RESPONSE";
    public static final String KEY = "eTDyuwJ9uTUcv09J8tONAlDwNtFqeQ4Dy5GXFuJO";
    public static final String IMAGE_URL = "https://api.500px.com/v1/photos?feature=fresh_today&image_size=3&consumer_key=" + KEY;
    public static final String PERCENT = "percent";

    public DownloadService() {
        super(SERVICE_NAME);
    }

    byte[] getByteImage(URL url) throws Exception {
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendBroadcast(
                (new Intent())
                        .setAction(ACTION_RESPONSE)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .putExtra(PERCENT, 0)
        );

        try {
            getContentResolver().delete(
                    DataBaseProvider.IMAGE_URI,
                    null,
                    null
            );

            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(IMAGE_URL)).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            JSONObject json = new JSONObject(bufferedReader.readLine());
            JSONArray jsonImages = json.getJSONArray("photos");

            for (int i = 0; i < jsonImages.length(); i++) {
                JSONObject image = jsonImages.getJSONObject(i);
                String name = image.getString("name");
                ContentValues cv = new ContentValues();
                byte[] array = getByteImage(new URL(image.getString("image_url")));
                cv.put(ImagesContract.COLUMN_IMAGE, array);
                cv.put(ImagesContract.COLUMN_IMAGE_NAME, name);
                getContentResolver().insert(DataBaseProvider.IMAGE_URI, cv);
                sendBroadcast(
                        (new Intent())
                                .setAction(ACTION_RESPONSE)
                                .addCategory(Intent.CATEGORY_DEFAULT)
                                .putExtra(PERCENT, Math.round((float) ((i + 1) * 100.0 / jsonImages.length()))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
