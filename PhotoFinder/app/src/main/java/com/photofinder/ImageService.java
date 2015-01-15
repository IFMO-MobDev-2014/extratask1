package com.photofinder;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ImageService extends IntentService {
    public static final String ACTION_UPDATE = "com.photofinder.UPDATE";
    public static final String ACTION_READY = "com.photofinder.READY";
    public static final String ACTION_ADD = "com.photofinder.ADD";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    public static final String EXTRA_KEY_ADD = "EXTRA_ADD";
    public static final String EXTRA_KEY_SUCCESS = "EXTRA_SUCCESS";
    public static final String EXTRA_KEY_PROGRESS = "PROGRESS";
    public static final String EXTRA_KEY_LINK = "LINK";
    public static final String EXTRA_KEY_XXL_LINK = "XXL_LINK";
    public static final String URL = "http://api-fotki.yandex.ru/api/recent/?format=json";

    public ImageService() {
        super("ImageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent.getBooleanExtra("JOB", true)) {
                StringBuilder stringBuilder = new StringBuilder();
                URLConnection urlConnection = new URL(URL).openConnection();
                urlConnection.setConnectTimeout(10000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    stringBuilder.append(s).append("\n");
                }
                bufferedReader.close();
                JSONArray entries = new JSONObject(stringBuilder.toString()).getJSONArray("entries");
                for (int i = 0; i < getResources().getInteger(R.integer.pictures_count); ++i) {
                    JSONObject entry = entries.getJSONObject(i);
                    String link = entry.getJSONObject("img").getJSONObject("M").getString("href");
                    Bitmap image = BitmapFactory.decodeStream(new URL(link).openConnection().getInputStream());
                    Intent intentUpdate = new Intent();
                    intentUpdate.setAction(ACTION_UPDATE);
                    intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                    intentUpdate.putExtra(EXTRA_KEY_UPDATE, image);
                    intentUpdate.putExtra(EXTRA_KEY_PROGRESS, i);
                    intentUpdate.putExtra(EXTRA_KEY_LINK, entry.getJSONObject("links").getString("alternate"));
                    intentUpdate.putExtra(EXTRA_KEY_XXL_LINK, entry.getJSONObject("img").getJSONObject("XL").getString("href"));
                    sendBroadcast(intentUpdate);
                }
                Intent intentReady = new Intent();
                intentReady.setAction(ACTION_READY);
                intentReady.addCategory(Intent.CATEGORY_DEFAULT);
                intentReady.putExtra(EXTRA_KEY_SUCCESS, true);
                sendBroadcast(intentReady);
            } else {
                Bitmap image = BitmapFactory.decodeStream(new URL(intent.getStringExtra("XXL_LINK")).openConnection().getInputStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                Intent intentUpdate = new Intent();
                intentUpdate.setAction(ACTION_ADD);
                intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                intentUpdate.putExtra(EXTRA_KEY_ADD, bytes);
                sendBroadcast(intentUpdate);
            }
        } catch (Exception e) {
            Intent intentReady = new Intent();
            intentReady.setAction(ACTION_READY);
            intentReady.addCategory(Intent.CATEGORY_DEFAULT);
            intentReady.putExtra(EXTRA_KEY_SUCCESS, false);
            sendBroadcast(intentReady);
        }
    }

    private Bitmap downloadPicture(String source) {
        try {
            URL url = new URL(source);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input;
            input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            return null;
        }

    }


}
