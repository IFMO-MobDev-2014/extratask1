package ru.ifmo.md.extratask1.api500px;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import ru.ifmo.md.extratask1.activities.MainActivity;
import ru.ifmo.md.extratask1.Photo;

/**
 * Created by pinguinson on 12.10.2014.
 */
public class FiveHundredSearchTask extends AsyncTask<Void, Void, ArrayList<Photo>> {
    public static final String TAG = "500px";
    public static final String API_KEY = "I9xyEPPXQKl7cyuhps5UCQr8AmHcne2rOi6f1TOa";
    MainActivity activity;

    public FiveHundredSearchTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Photo> doInBackground(Void... voids) {
        FiveHundredQuery fiveHundredQuery = new FiveHundredQuery(API_KEY);
        fiveHundredQuery.addParameter("image_size[]", "4");
        fiveHundredQuery.addParameter("rpp", String.valueOf(MainActivity.MAX_IMAGES));
        ArrayList<Photo> pictures = new ArrayList<>();
        try {
            JSONArray searchResults = fiveHundredQuery.get().getJSONArray("photos");
            for (int i = 0; i < searchResults.length(); ++i) {
                JSONArray current = searchResults.getJSONObject(i).getJSONArray("images");
                String fullUrl = current.getJSONObject(0).getString("https_url");
                pictures.add(new Photo(fullUrl));
                if (pictures.size() == MainActivity.MAX_IMAGES) {
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Search failed", e);
        }
        return pictures;
    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        super.onPostExecute(photos);
        activity.onImageSearchFinished(photos);
    }
}