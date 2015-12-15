package ru.ifmo.md.photooftheday.photodownloader;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public class JSONDownloadTask extends AsyncTask<Void, Void, JSONObject> {
    public static final String TAG = JSONDownloadTask.class.getSimpleName();

    public static final int IMAGES_TO_DOWNLOAD = 20;
    private static final String API_URL = "https://api.500px.com/v1/photos";
    private static final String CONSUMER_KEY = "EeLvrefbDq9ZgIhCNk95vSjOMHwaogEGkJjNRayk";

    private static String feature = "fresh_today";
    private static String sort = "highest_rating";
    private static String sortDirection = "desc";
    private static String counter = Integer.toString(IMAGES_TO_DOWNLOAD);
    private static String[] imageSizes = {"2", "4"};
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
        JSONObject object = null;
        try {
            object = handle(url);
        } catch (IOException e) {
            Log.e(TAG, "Error obtaining response from 500px api.", e);
        }
        Log.d(TAG, "doInBackground() complete in " + (System.currentTimeMillis() - startTime) + ".ms");
        return object;
    }

    // copy-paste from https://github.com/500px/500px-android-sdk/blob/master/src/main/java/com/fivehundredpx/api/PxApi.java
    private JSONObject handle(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        JSONObject result = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }
            result = new JSONObject(total.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSONObject", e);
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }
}
