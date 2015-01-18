package daria.extratask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by daria on 18.01.15.
 */
public class PhotoSearchTask extends AsyncTask<Void, Void, ArrayList<Photo>> {
    PhotosActivity activity;
    public static final String TAG = "500px";

    public static final String API_KEY = "GW8UFBIcTAAidXLYXun9kVIj1NV5OfYRhjPshygw";

    public PhotoSearchTask(PhotosActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Photo> doInBackground(Void... voids) {

        PhotoQuery fiveHundredQuery = new PhotoQuery(API_KEY);
        fiveHundredQuery.addParameter("image_size[]", "3");
        fiveHundredQuery.addParameter("image_size[]", "4");
        fiveHundredQuery.addParameter("rpp", "30");
        ArrayList<Photo> pictures = new ArrayList<>();
        try {
            JSONArray searchResults = fiveHundredQuery.get().getJSONArray("photos");
            for (int i = 0; i < searchResults.length(); ++i) {
                JSONArray current = searchResults.getJSONObject(i).getJSONArray("images");
                String previewUrl = current.getJSONObject(0).getString("https_url");
                String fullUrl = current.getJSONObject(1).getString("https_url");
                Log.i("preview", previewUrl);
                pictures.add(new Photo(fullUrl, previewUrl));
                if (pictures.size() == 30) {
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
