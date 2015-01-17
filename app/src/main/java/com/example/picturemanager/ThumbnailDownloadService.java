package com.example.picturemanager;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Амир on 07.12.2014.
 */

public class ThumbnailDownloadService extends IntentService {

    public static final String LOAD_FINISHED_BROADCAST = "Load finished";

    private static final String CONSUMER_KEY = "nMEqt1FOd6Mqdjn9pDWQyjmzQDEotswvbDgakC9z";
    private static final String CONSUMER_SECRET = "ulOHRSaaFPmRnjW0fS70DjArOUtvZNFGeV8XMK6I";
    public static final String BASE_URL = "https://api.500px.com/v1/photos";
    public static final int imagesPerPage = 16;
    public static final int imageSize = 2;

    public ThumbnailDownloadService() {
        super("ThumbnailDownloadService");
    }

    private String getJsonString(int pageNumber, String category) {
        String request = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("feature", category)
                .appendQueryParameter("sort", "rating")
                .appendQueryParameter("image_size", String.valueOf(imageSize))
                .appendQueryParameter("include_store", "store_download")
                .appendQueryParameter("include_states", "voted")
                .appendQueryParameter("page", String.valueOf(pageNumber))
                .appendQueryParameter("rpp", String.valueOf(imagesPerPage))
                .appendQueryParameter("consumer_key", CONSUMER_KEY)
                .toString();
        String jsonString = "";
        //Log.d("REQUEST", request);
        try {
            URL url = new URL(request);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                jsonString += line;
                line = reader.readLine();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String category = intent.getStringExtra("category");
        int pageNumber = intent.getIntExtra("pageNumber", 1);
        String jsonString = getJsonString(pageNumber, category);
        //Log.d("JSONSTRING", jsonString);
        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);
            String selection = DBHelper.PICTURES_CATEGORY + " = \'" + category + "\' and " + DBHelper.PICTURES_PAGE + " = " + pageNumber;
            Log.d("selection", selection);
            getContentResolver().delete(DBContentProvider.PICTURES, selection, null);
            for (int i = 0; i < imagesPerPage; i++) {
                String link = json.getJSONArray("photos").getJSONObject(i).getString("image_url");
                String name = json.getJSONArray("photos").getJSONObject(i).getString("name");
                URL imageURL = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] bArray = outputStream.toByteArray();
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.PICTURES_NAME, name);
                cv.put(DBHelper.PICTURES_HAS_BIG_PICTURE, false);
                cv.put(DBHelper.PICTURES_SMALL_PICTURE, bArray);
                cv.put(DBHelper.PICTURES_PAGE, pageNumber);
                cv.put(DBHelper.PICTURES_CATEGORY, category);
                getContentResolver().insert(DBContentProvider.PICTURES, cv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent broadcast = new Intent();
        broadcast.setAction(LOAD_FINISHED_BROADCAST);
        broadcast.putExtra("category", category);
        broadcast.putExtra("pageNumber", pageNumber);
        sendBroadcast(broadcast);
        Log.d("BROADCAST", "SENT!");
    }

}
