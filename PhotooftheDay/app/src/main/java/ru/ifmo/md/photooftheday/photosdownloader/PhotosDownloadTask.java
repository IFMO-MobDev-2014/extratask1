package ru.ifmo.md.photooftheday.photosdownloader;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by vadim on 17/01/15.
 */
public class PhotosDownloadTask extends AsyncTask<PhotosParams, Void, List<Bitmap>> {
    public static final String TAG = "PhotosDownloadTask";

    private static final String API_URL = "https://api.500px.com/v1/photos";
    private static final String CONSUMER_KEY = "EeLvrefbDq9ZgIhCNk95vSjOMHwaogEGkJjNRayk";

    @Override
    protected List<Bitmap> doInBackground(final PhotosParams... params) {
        final PhotosParams param = params[0];
        final String request = API_URL + "?" +
                "feature=" + param.feature + "&" +
                "sort=" + param.sort + "&" +
                "sort_direction=" + param.sortDirection + "&" +
                "only=" + param.only + "&" +
                "exclude=" + param.exclude + "&" +
                "rpp=" + param.counter + "&" +
                "image_size" + param.imageSize + "&" +
                "consumer_key=" + CONSUMER_KEY;
        Log.v(TAG, "URL: " + request);
        JSONObject object = handle(new HttpGet(request));

        try {
            if (object != null) {
                JSONArray photos = object.getJSONArray("photos");
                List<Bitmap> result = new ArrayList<>();
                for (int i = 0; i < photos.length(); ++i) {
                    URL url = new URL(photos.getJSONObject(0).getString("image_url"));
                    InputStream inputStream = url.openStream();
                    result.add(BitmapFactory.decodeStream(inputStream));
                }
                return result;
            } else {
                Log.w(TAG, "failed to download JSONObject");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /* copy-paste from https://github.com/500px/500px-android-sdk/blob/master/src/main/java/com/fivehundredpx/api/PxApi.java */
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
