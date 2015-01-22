package ru.ifmo.md.photooftheday;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import java.util.concurrent.ExecutionException;

import ru.ifmo.md.photooftheday.memoryutils.FilesUtils;
import ru.ifmo.md.photooftheday.photodatabase.PhotoContract;
import ru.ifmo.md.photooftheday.photodatabase.PhotoProvider;
import ru.ifmo.md.photooftheday.photodownloader.JSONDownloadTask;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MainActivity.class.getSimpleName();

    /* package-private */ static final String PHOTO = MainActivity.class.getCanonicalName() + "/PHOTO";

    private static final int PORTRAIT_COLUMNS_COUNTER = 3;
    private static final int LANDSCAPE_COLUMNS_COUNTER = 7;

    private int columnsCounter;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() was called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnsCounter = LANDSCAPE_COLUMNS_COUNTER;
        } else {
            columnsCounter = PORTRAIT_COLUMNS_COUNTER;
        }
        Log.d(TAG, "columnsCounter = " + columnsCounter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnsCounter));
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, PORTRAIT_COLUMNS_COUNTER);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(new ArrayList<Photo>()){
            @Override
            public void onBindViewHolder(Holder holder, final int position) {
                final Photo photo = dataset.get(position);
                holder.imageView.setImageBitmap(photo.getThumbnailBitmap());
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("OnClickListener", "click");
                        Intent intent = new Intent(MainActivity.this, DisplayPhotoActivity.class);
                        intent.putExtra(PHOTO, photo);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged() was called with orientation " + newConfig.orientation +
                " (landscape = " + Configuration.ORIENTATION_LANDSCAPE +
                ", portrait = " + Configuration.ORIENTATION_PORTRAIT +
                ", undefined = " + Configuration.ORIENTATION_UNDEFINED + ")");
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnsCounter = LANDSCAPE_COLUMNS_COUNTER;
        } else {
            columnsCounter = PORTRAIT_COLUMNS_COUNTER;
        }
        if (recyclerView != null) {
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(columnsCounter);
        }
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
        if (id == R.id.action_reload) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: progress bar
    public void refresh() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONDownloadTask().execute().get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (jsonObject == null) {
            Toast.makeText(this, getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerAdapter.clear();
        ContentValues values = new ContentValues();
        values.put(PhotoContract.Photo.VALID_STATE, 0);
        getContentResolver().update(PhotoProvider.PHOTO_CONTENT_URI, values, null, null);

        try {
            JSONArray photos = jsonObject.getJSONArray("photos");
            int insertCounter = 0;
            for (int i = 0; i < photos.length(); ++i) {
                final JSONObject jsonPhoto = photos.getJSONObject(i);
                final JSONArray images = jsonPhoto.getJSONArray("images");
                JSONObject jsonThumbnail = images.getJSONObject(0);
                JSONObject jsonFull = images.getJSONObject(1);

                if (!(jsonFull.getInt("size") == 4 && jsonThumbnail.getInt("size") == 2)) throw new AssertionError();
                final String thumbnailUrl = jsonThumbnail.getString("url");
                final String fullUrl = jsonFull.getString("url");

                final String photoID = jsonPhoto.getString("id");
                final String photoName = jsonPhoto.getString("name");

                Photo photo = new Photo(photoName, photoID, new URL(thumbnailUrl), new URL(fullUrl));
                recyclerAdapter.add(photo);

                Cursor cursor = getContentResolver().query(PhotoProvider.PHOTO_CONTENT_URI, null,
                        PhotoContract.Photo.ID + " = " + DatabaseUtils.sqlEscapeString(photoID) + " AND " +
                                PhotoContract.Photo.NAME + " = " + DatabaseUtils.sqlEscapeString(photoName) + " AND " +
                                PhotoContract.Photo.URL_FULL + " = " + DatabaseUtils.sqlEscapeString(fullUrl) + " AND " +
                                PhotoContract.Photo.URL_THUMBNAIL + " = " + DatabaseUtils.sqlEscapeString(thumbnailUrl),
                        null, null
                        );
                if (cursor.getCount() == 0) {
                    insertCounter++;
                    values.clear();
                    values.put(PhotoContract.Photo.ID, photoID);
                    values.put(PhotoContract.Photo.NAME, photoName);
                    values.put(PhotoContract.Photo.URL_FULL, fullUrl);
                    values.put(PhotoContract.Photo.URL_THUMBNAIL, thumbnailUrl);
                    values.put(PhotoContract.Photo.VALID_STATE, 1);
                    getContentResolver().insert(PhotoProvider.PHOTO_CONTENT_URI, values);
                } else {
                    values.clear();
                    values.put(PhotoContract.Photo.VALID_STATE, 1);
                    getContentResolver().update(PhotoProvider.PHOTO_CONTENT_URI, values, null, null);
                }
                cursor.close();
            }
            Cursor cursor = getContentResolver().query(PhotoProvider.PHOTO_CONTENT_URI, PhotoContract.Photo.ALL_COLUMNS,
                    PhotoContract.Photo.VALID_STATE + " = 0", null, null);
            int ddeleteCounter = cursor.getCount();
            while (!cursor.isAfterLast()) {
                String photoID = cursor.getString(cursor.getColumnIndex(PhotoContract.Photo.ID));
                FilesUtils.removeFile(FilesUtils.getApplicationStorageDir(), photoID + Photo.THUMBNAIL_SUFFIX);
                FilesUtils.removeFile(FilesUtils.getApplicationStorageDir(), photoID);
                cursor.moveToNext();
            }
            cursor.close();
            int deleteCounter = getContentResolver().delete(PhotoProvider.PHOTO_CONTENT_URI,
                    PhotoContract.Photo.VALID_STATE + " = 0", null);
            Log.d(TAG, "After updating " + insertCounter + " rows were inserted " +
                    "and " +  deleteCounter + "(" + ddeleteCounter + ") were deleted");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /* LoaderCallback methods */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PhotoProvider.PHOTO_CONTENT_URI,
                PhotoContract.Photo.ALL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            refresh();
        } else {
            data.moveToFirst();
            recyclerAdapter.clear();
            while (!data.isAfterLast()) {
                final String photoName = data.getString(data.getColumnIndex(PhotoContract.Photo.NAME));
                final String photoID = data.getString(data.getColumnIndex(PhotoContract.Photo.ID));
                final String thumbnailUrl = data.getString(data.getColumnIndex(PhotoContract.Photo.URL_THUMBNAIL));
                final String fullUrl = data.getString(data.getColumnIndex(PhotoContract.Photo.URL_FULL));
                Photo photo = null;
                try {
                    photo = new Photo(photoName, photoID, new URL(thumbnailUrl), new URL(fullUrl));
                } catch (MalformedURLException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                if (photo != null) {
                    recyclerAdapter.add(photo);
                } else {
                    Log.w(TAG, "couldn't load photo from database (maybe URL is incorrect)");
                }
                data.moveToNext();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset() was called");
    }
}
