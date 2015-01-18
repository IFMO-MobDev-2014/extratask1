package com.example.heat_wave.photoviewer.tasks;

import android.os.AsyncTask;
import android.util.Log;


import com.example.heat_wave.photoviewer.auxiliary.FiveHundredQuery;
import com.example.heat_wave.photoviewer.models.Photo;
import com.example.heat_wave.photoviewer.*;
import org.json.JSONArray;

import java.util.ArrayList;
/**
 * Created by heat_wave on 17.01.15.
 */
public class FiveHundredSearchTask extends AsyncTask<Void, Void, ArrayList<Photo>> {
    ViewerActivity activity;
    public static final String TAG = "500px";

    public static final String API_KEY = "GW8UFBIcTAAidXLYXun9kVIj1NV5OfYRhjPshygw";

    public FiveHundredSearchTask(ViewerActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Photo> doInBackground(Void... voids) {

        FiveHundredQuery fiveHundredQuery = new FiveHundredQuery(API_KEY);
        fiveHundredQuery.addParameter("image_size[]", "3");
        fiveHundredQuery.addParameter("image_size[]", "4");
        ArrayList<Photo> pictures = new ArrayList<>();
        try {
            JSONArray searchResults = fiveHundredQuery.get().getJSONArray("photos");
            for (int i = 0; i < searchResults.length(); ++i) {
                //String url = searchResults.getJSONObject(i).getJSONObject("url").toString();
                JSONArray current = searchResults.getJSONObject(i).getJSONArray("images");
                String previewUrl = current.getJSONObject(0).getString("https_url");
                String fullUrl = current.getJSONObject(1).getString("https_url");
                Log.i("preview", previewUrl);
                //pictures.add(new Photo(url, fullUrl, previewUrl));
                pictures.add(new Photo(fullUrl, previewUrl));
                if (pictures.size() == 20) {
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Search failed.", e);
        }
        return pictures;
    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        super.onPostExecute(photos);
        activity.onSearchFinished(photos);
    }
}