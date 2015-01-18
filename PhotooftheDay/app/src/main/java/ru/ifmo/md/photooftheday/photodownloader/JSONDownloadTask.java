package ru.ifmo.md.photooftheday.photodownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 17/01/15.
 */
public class JSONDownloadTask extends AsyncTask<Void, Void, JSONObject> {
    public static final String TAG = JSONDownloadTask.class.getSimpleName();

    private static final String API_URL = "https://api.500px.com/v1/photos";
    private static final String CONSUMER_KEY = "EeLvrefbDq9ZgIhCNk95vSjOMHwaogEGkJjNRayk";

    private static String feature = "fresh_today";
    private static String sort = "highest_rating";
    private static String sortDirection = "desc";
    private static String counter = "20";
    private static String[] imageSizes = {"3", "4"};
//    private static String only = "0";
//    private static String exclude = "0";

    private static String request;
    static {
        request = "feature=" + feature + "&" +
                "sort=" + sort + "&" +
                "sort_direction=" + sortDirection + "&" +
                "rpp=" + counter;
        for (String imageSize : imageSizes) {
            request += "&image_size[]=" + imageSize;
        }
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground() starts");
        long startTime = System.currentTimeMillis();
        final String url = API_URL + "?" + request + "&consumer_key=" + CONSUMER_KEY;
        Log.v(TAG, "URL: " + url);
        JSONObject object = handle(new HttpGet(url));
        Log.d(TAG, "doInBackground() complete in " + (System.currentTimeMillis() - startTime) + ".ms");
        return object;
    }

    // copy-paste from https://github.com/500px/500px-android-sdk/blob/master/src/main/java/com/fivehundredpx/api/PxApi.java
    private JSONObject handle(HttpUriRequest request) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();

            HttpResponse response = client.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                final String msg = String.format(
                        "Error, statusCode not OK(%d). for url: %s",
                        statusCode, request.getURI().toString());
                Log.e(TAG, msg);
                return null;
            }

            HttpEntity responseEntity = response.getEntity();
            InputStream inputStream = responseEntity.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            JSONObject json = new JSONObject(total.toString());
            return json;
        } catch (Exception e) {
            Log.e(TAG, "Error obtaining response from 500px api.", e);
        }
        return null;
    }
}
