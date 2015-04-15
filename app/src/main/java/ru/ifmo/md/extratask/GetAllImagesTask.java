package ru.ifmo.md.extratask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by gshark on 16.03.15
 */
class GetAllImagesTask extends AsyncTask<Void, Integer, Void> {
    public static final String YANDEX_PHOTO_API = "http://api-fotki.yandex.ru/api/top/";
    public static final String LOG_TAG = GetAllImagesTask.class.getSimpleName();
    public static final String JSON_PARSE_ERROR = "Couldn't parse JSON";

    private final Context context;
    private final RecyclerView.Adapter<ImageViewHolder> adapter;
    private final List<Photo> photos;
    private final List<Bitmap> bitmaps;
    private final boolean update;
    private boolean exceptionHappened = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GetAllImagesTask(Context context,
                            RecyclerView.Adapter<ImageViewHolder> adapter,
                            List<Photo> photos,
                            List<Bitmap> bitmaps,
                            boolean update,
                            SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.adapter = adapter;
        this.photos = photos;
        this.bitmaps = bitmaps;
        this.update = update;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (exceptionHappened) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.dialog_error)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.btn_dialog)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //System.exit(0);
                        }
                    }) //TODO: remove
                    .create()
                    .show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(YANDEX_PHOTO_API);
        httpGet.setHeader("Accept", "application/json");

        JSONObject jsonObject = null;
        if (update) {
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String response = EntityUtils.toString(httpResponse.getEntity());
                jsonObject = new JSONObject(response);
            } catch (Exception e) {
                Log.e(LOG_TAG, JSON_PARSE_ERROR);
                exceptionHappened = true;
            }
        }
        if (update && !exceptionHappened) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("entries");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject pic = jsonArray.getJSONObject(i);
                    JSONObject img = pic.getJSONObject("img");
                    boolean noOrig = pic.getBoolean("hideOriginal");
                    String orig;
                    if (noOrig) {
                        orig = img.getJSONObject("XXXL").getString("href");
                    } else {
                        orig = img.getJSONObject("orig").getString("href");
                    }
                    photos.set(i, new Photo(
                            pic.getString("id"),
                            img.getJSONObject("M").getString("href"),
                            img.getJSONObject("XXL").getString("href"),
                            orig,
                            pic.getJSONObject("links").getString("alternate")
                    ));

                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, JSON_PARSE_ERROR);
                exceptionHappened = true;
            }
        }

        File extDir = context.getExternalFilesDir(null);

        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            if (photo == null) {
                break;
            }
            final File file = new File(extDir, photo.getId() + "-preview");
            if (!file.exists()) {
                final int j = i;
                (new ImageDownloadTask(file, new Runnable() {
                    @Override
                    public void run() {
                        bitmaps.set(j, BitmapFactory.decodeFile(file.getAbsolutePath()));
                        publishProgress(j);
                    }
                })).execute(photo.getPreview());
            } else {
                bitmaps.set(i, BitmapFactory.decodeFile(file.getAbsolutePath()));
                publishProgress(i);
            }
        }
        return null;
    }
}
