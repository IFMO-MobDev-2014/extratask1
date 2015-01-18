package ru.ifmo.md.photooftheday;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.ifmo.md.photooftheday.memoryutils.LoadBitmapTask;
import ru.ifmo.md.photooftheday.memoryutils.SaveBitmapTask;
import ru.ifmo.md.photooftheday.photodatabase.PhotoContract;
import ru.ifmo.md.photooftheday.photodatabase.PhotoProvider;
import ru.ifmo.md.photooftheday.photodownloader.BitmapDownloadTask;
import ru.ifmo.md.photooftheday.photodownloader.JSONDownloadTask;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MainActivity.class.getSimpleName();

    /* package-private */ static final String IMAGE_TITLE = MainActivity.class.getCanonicalName() + "/IMAGE_TITLE";

    private static final int PORTRAIT_COLUMNS_COUNTER = 2;
    private static final int LANDSCAPE_COLUMNS_COUNTER = 4;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "hello!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, PORTRAIT_COLUMNS_COUNTER));
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, PORTRAIT_COLUMNS_COUNTER);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(new ArrayList<Bitmap>(), new ArrayList<String>()){
            @Override
            public void onBindViewHolder(Holder holder, final int position) {
                Bitmap bitmap = dataset.get(position);
                holder.imageView.setImageBitmap(bitmap);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("OnClickListener", "click");
                        Intent intent = new Intent(MainActivity.this, DisplayPhotoActivity.class);
                        String photoID = datasetTitles.get(position);
                        intent.putExtra(IMAGE_TITLE, photoID);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerAdapter);

        getLoaderManager().initLoader(0, null, this);
//        recyclerAdapter.add(0, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//
//        try {
//            List<Bitmap> list = new PhotosDownloadTask().execute(new PhotosParams.Builder().setCounter(10).setImageSize(1).build()).get();
//            if (list != null) {
//                for (int i = 0; i < list.size(); ++i) {
//                    recyclerAdapter.add(i + 1, list.get(i));
//                }
//            } else {
//                Log.d(TAG, "Failed download");
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Cursor refresh() {
        /*
        download json
        parse it and update database
        download thumbnail one by one
        return all
         */
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONDownloadTask().execute().get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (jsonObject == null) {
            Toast.makeText(this, "Failed to download photos", Toast.LENGTH_SHORT);
            return null;
        }

        try {
            JSONArray photos = jsonObject.getJSONArray("photos");
            Log.d(TAG, "photos in JSON: " + photos.length());
            for (int i = 0; i < photos.length(); ++i) {
                final JSONObject photo = photos.getJSONObject(i);
                final JSONArray images = photo.getJSONArray("images");
                JSONObject thumbnail = images.getJSONObject(0);
                JSONObject full = images.getJSONObject(1);

                Log.d(TAG, "full size = " + (full.getInt("size")) + " and thumbnail size = " + (thumbnail.getInt("size")));
                if (!(full.getInt("size") == 4 && thumbnail.getInt("size") == 2)) throw new AssertionError();
                String thumbnailUrl = thumbnail.getString("url");
                String fullUrl = full.getString("url");

                final String photoID = photo.getString("id");

                new BitmapDownloadTask(){
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        recyclerAdapter.add(bitmap, photoID);
                        new SaveBitmapTask(photoID + "-thumbnail").execute(bitmap);
                    }
                }.execute(new URL(thumbnailUrl));

                // TODO: make it lazy
                new BitmapDownloadTask(){
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        new SaveBitmapTask(photoID).execute(bitmap);
                    }
                }.execute(new URL(fullUrl));

                ContentValues values = new ContentValues();
                values.put(PhotoContract.Photo.TITLE, photoID);
                values.put(PhotoContract.Photo.NAME, photo.getString("name"));
                values.put(PhotoContract.Photo.URL_FULL, fullUrl);
                values.put(PhotoContract.Photo.URL_THUMBNAIL, thumbnailUrl);
                values.put(PhotoContract.Photo.VALID_STATE, 1);

                getContentResolver().update(PhotoProvider.PHOTO_CONTENT_URI, values, null, null);
                // TODO: remove all invalid images
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, "Failed to download photos", Toast.LENGTH_SHORT);
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return getContentResolver().query(PhotoProvider.PHOTO_CONTENT_URI,
                PhotoContract.Photo.ALL_COLUMNS, null, null, null);
    }

    // LoaderCallback methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PhotoProvider.PHOTO_CONTENT_URI,
                PhotoContract.Photo.ALL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (data == null) throw new AssertionError("Cursor is null");
        if (data == null) {
            Log.d(TAG, "go to refresh");
            data = refresh();
        }
        Log.d(TAG, "data is " + (data == null ? "" : "not ") + "null");
        data.moveToFirst();
        Log.d(TAG, "recyclerAdapter is " + (recyclerAdapter == null ? "" : "not ") + "null");
        Log.d(TAG, "recyclerAdapter.size() = " + recyclerAdapter.getItemCount());
        recyclerAdapter.clear();
        while (!data.isAfterLast()) {
            final String title = data.getString(data.getColumnIndex(PhotoContract.Photo.TITLE));
            new LoadBitmapTask(title + "-thumbnail"){
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    recyclerAdapter.add(bitmap, title);
                }
            }.execute();
            data.moveToNext();
        }
        Log.d(TAG, "recyclerAdapter.size() = " + recyclerAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        throw new UnsupportedOperationException(TAG + ".onLoaderReset()");
    }
}
