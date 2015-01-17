package com.example.maksim.photoview;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader extends IntentService {

    public final String Url = "http://api-fotki.yandex.ru/api/podhistory/?format=json";

    public ImageDownloader() {
        super("service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent response = new Intent();
        response.setAction("RESPONSE");
        response.addCategory(Intent.CATEGORY_DEFAULT);
        response.putExtra("percent", 0);
        sendBroadcast(response);
        try {
            URL addr = new URL(Url);
            URLConnection connection = addr.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
            String current;
            while ((current = reader.readLine()) != null) {
                buffer.append(current + "\n");
            }
            reader.close();
            String json = buffer.toString();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray images = jsonObject.getJSONArray("entries");

            getContentResolver().delete(MyContentProvider.IMAGES_CONTENT_URI, null, null);

            for (int i = 0; i < images.length(); i++)
            {
                JSONObject currentImage = images.getJSONObject(i);
                URL currentSmallURL = new URL(currentImage.getJSONObject("img").getJSONObject("M").getString("href"));
                //URL currentLargeURL = new URL(currentImage.getJSONObject("img").getJSONObject("M").getString("href"));
                Bitmap smallBitmap = BitmapFactory.decodeStream(currentSmallURL.openConnection().getInputStream());
                //Bitmap largeBitmap = BitmapFactory.decodeStream(currentLargeURL.openConnection().getInputStream());
                ByteArrayOutputStream bosSmall = new ByteArrayOutputStream();
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 100, bosSmall);
                byte[] arraySmall = bosSmall.toByteArray();
                //ByteArrayOutputStream bosLarge = new ByteArrayOutputStream();
                //largeBitmap.compress(Bitmap.CompressFormat.PNG, 100, bosLarge);
                //byte[] arrayLarge = bosLarge.toByteArray();
                ContentValues cv = new ContentValues();
                //cv.put(SQLiteHelper.LARGE_IMAGE, arrayLarge);

                String largeImageLink = currentImage.getJSONObject("img").getJSONObject("XL").getString("href");
                cv.put(SQLiteHelper.LARGE_IMAGE, largeImageLink);

                cv.put(SQLiteHelper.SMALL_IMAGE, arraySmall);
                getContentResolver().insert(MyContentProvider.IMAGES_CONTENT_URI, cv);
                int progress = 100 * (i + 1) / images.length();
                response = new Intent();
                response.setAction("RESPONSE");
                response.addCategory(Intent.CATEGORY_DEFAULT);
                response.putExtra("percent", progress);
                sendBroadcast(response);
            }
        } catch (Exception e) {
            response = new Intent();
            response.setAction("RESPONSE");
            response.addCategory(Intent.CATEGORY_DEFAULT);
            response.putExtra("percent", -1);
            sendBroadcast(response);
        }
    }
}
