package ru.ifmo.md.extratask1;

import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ImageSearchTask extends AsyncTask<Void, Void, ArrayList<String>> {
    ResultsList screen;

    public ImageSearchTask(ResultsList screen) {
        this.screen = screen;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... sth) {
        String uri = Uri.parse("http://api-fotki.yandex.ru/api/recent/")
                .buildUpon()
                .appendQueryParameter("limit", "50")
                .appendQueryParameter("format", "json")
                .build().toString();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(uri);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        JSONObject responseJson;
        ArrayList<String> urls = new ArrayList<>();

        try {
            String responseStr = client.execute(request, responseHandler);
            try {
                responseJson = new JSONObject(responseStr);
                JSONArray results = responseJson.getJSONArray("entries");
                int resultsCount = results.length();

                for (int i = 0; i < resultsCount; i++) {
                    JSONObject result = results.getJSONObject(i);
                    JSONObject images = result.getJSONObject("img");
                    String photoUrl = images.getJSONObject("M").getString("href");
                    urls.add(photoUrl);
                }
            } catch (JSONException ignore) {
            } // Yandex.Fotki JSON is a valid JSON
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urls;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        screen.onImageSearchCancelled();
    }

    @Override
    protected void onPostExecute(ArrayList<String> urls) {
        super.onPostExecute(urls);
        screen.onImageSearchFinished(urls);
    }
}
