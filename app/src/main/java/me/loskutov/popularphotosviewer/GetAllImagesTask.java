package me.loskutov.popularphotosviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
 * Created by ignat on 16.01.15.
 */
class GetAllImagesTask extends AsyncTask <Void, Integer, Void> {
    private final Context context;
    private final RecyclerView.Adapter<ImageViewHolder> adapter;
    private final List<Photo> photos;
    private final List<Bitmap> bitmaps;
    private final boolean update;
    private ProgressDialog progressDialog;

    public GetAllImagesTask(Context context,
                            RecyclerView.Adapter<ImageViewHolder> adapter,
                            List<Photo> photos,
                            List<Bitmap> bitmaps,
                            boolean update) {
        Log.d("lal", "GetAllImagesTask");
        this.context = context;
        this.adapter = adapter;
        this.photos = photos;
        this.bitmaps = bitmaps;
        this.update = update;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        adapter.notifyDataSetChanged();
        if (update) {
            progressDialog.setProgress(values[0] + 1);
        }
        Log.d("notified", values[0].toString());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (update) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(R.string.progress_title);
            progressDialog.setMessage(context.getString(R.string.progress_message));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(50);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        adapter.notifyDataSetChanged();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Log.d("aa", "got all images");
        Log.d("aa", "and size is " + photos.size());
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.d("doinbg:", "" + photos.size());

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://api-fotki.yandex.ru/api/top/");
        httpGet.setHeader("Accept", "application/json");

        boolean exceptionHappened = false;
        JSONObject jsonObject = null;
        if(update) {
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String response = EntityUtils.toString(httpResponse.getEntity());
                jsonObject = new JSONObject(response);
            } catch (Exception e) {
                e.printStackTrace();
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
                    if(noOrig) {
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
                e.printStackTrace();
            }
        }

        // photos now contains all the needed information about the photos

        File extDir = context.getExternalFilesDir(null);

        for(int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            if(photo == null) {
                break;
            }
            final File file = new File(extDir, photo.id + "-preview");
            if(!file.exists()) {
                final int j = i;
                (new ImageDownloadTask(photo.preview, file, new Runnable() {
                    @Override
                    public void run() {
                        bitmaps.set(j, BitmapFactory.decodeFile(file.getAbsolutePath()));
                        publishProgress(j);
                    }
                })).execute();
            } else {
                bitmaps.set(i, BitmapFactory.decodeFile(file.getAbsolutePath()));
                publishProgress(i);
            }
        }
        return null;
    }
}
