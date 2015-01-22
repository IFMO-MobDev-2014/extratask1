package com.example.timur.photoclient;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PhotosActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private PhotoAdapter photoAdapter;
    private GridView gridView;
    private int currentProgress = 0;
    private int currentPage = 1;
    private boolean updating = false;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        progressBar = (ProgressBar) findViewById(R.id.progressBarHorizontal);
        gridView = (GridView) findViewById(R.id.gridView);
        List<Photo> listPhoto = new ArrayList<>();
        photoAdapter = new PhotoAdapter(listPhoto);
        gridView.setAdapter(photoAdapter);
        gridView.setOnItemClickListener(listener);
        getLoaderManager().initLoader(1, null, this);
        if (checkConnection()) {
            Intent intent = new Intent(getApplicationContext(), LoaderService.class);
            intent.putExtra(LoaderService.PAGE, currentPage);
            intent.setAction(LoaderService.ACTION_DOWNLOAD_PAGE);
            startService(intent);
        }
        LoaderService.setHandler(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == LoaderService.MESSAGE_PROGRESS) {
                    if (currentProgress == 0) {
                        updating = true;
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    currentProgress++;
                    if (currentProgress == progressBar.getMax()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        updating = false;
                    }
                    progressBar.setProgress(currentProgress);
                } else if (message.what == LoaderService.MESSAGE_FINISHED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    updating = false;
                }
                return true;
            }
        }));
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
            intent.putExtra(LoaderService.ID, photoAdapter.photos.get(position).getIndex());
            intent.putExtra(LoaderService.DATABASE_ID, photoAdapter.photos.get(position).getDatabaseIndex());
            intent.putExtra(LoaderService.PHOTO_PER_PAGE, progressBar.getMax());
            intent.putExtra(LoaderService.BROUSE, photoAdapter.photos.get(position).getBrowseUrl());
            intent.putExtra(LoaderService.TITLE, photoAdapter.photos.get(position).getAuthor());
            startActivity(intent);
        }
    };

    private Boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseContentProvider.PHOTOS_CONTENT_URI, null,
                PhotoTable.PAGE + " = " + currentPage, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        try {
            if (cursor.getCount() != 0) {
                photoAdapter.photos.clear();
                while (cursor.moveToNext()) {
                    photoAdapter.photos.add(new Photo(cursor.getString(2), cursor.getString(1),
                            cursor.getBlob(4), cursor.getString(8), cursor.getInt(0)));
                }
                photoAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        photoAdapter = new PhotoAdapter(new ArrayList<Photo>());
        gridView.setAdapter(photoAdapter);
    }

    public void changePage(View view) {
        int turn = 0;
        if (view.getId() == R.id.button_prev_page) {
            if (currentPage != 1) {
                turn = -1;
            } else {
                Toast.makeText(getApplicationContext(), R.string.change_page_problem, Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.button_next_page) {
            turn = 1;
        } else {
            return;
        }
        currentProgress = 0;
        currentPage += turn;
        photoAdapter = new PhotoAdapter(new ArrayList<Photo>());
        gridView.setAdapter(photoAdapter);
        gridView.setOnItemClickListener(listener);
        photoAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(1, null, PhotosActivity.this);
        if (checkConnection()) {
            Intent intent = new Intent(getApplicationContext(), LoaderService.class);
            intent.putExtra(LoaderService.PAGE, currentPage);
            intent.setAction(LoaderService.ACTION_DOWNLOAD_PAGE);
            startService(intent);
        }
    }

    public void refresh(View view) {
        if (checkConnection() && !updating) {
            Intent intent = new Intent(getApplicationContext(), LoaderService.class);
            intent.putExtra(LoaderService.UPDATE, true);
            intent.putExtra(LoaderService.PAGE, currentPage);
            intent.setAction(LoaderService.ACTION_DOWNLOAD_PAGE);
            startService(intent);
            updating = true;
            currentProgress = 0;
        }
    }

    public class PhotoAdapter extends BaseAdapter {

        public List<Photo> photos;

        public PhotoAdapter(List<Photo> data) {
            this.photos = data;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View photo = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo, parent, false);
            ImageView imageView = (ImageView) photo.findViewById(R.id.image);
            imageView.setImageBitmap(photos.get(position).getBitmap());
            return photo;
        }

    }
}
