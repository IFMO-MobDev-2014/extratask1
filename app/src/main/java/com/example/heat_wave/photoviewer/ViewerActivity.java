package com.example.heat_wave.photoviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.heat_wave.photoviewer.auxiliary.PhotoAdapter;
import com.example.heat_wave.photoviewer.auxiliary.SpacesItemDecoration;
import com.example.heat_wave.photoviewer.database.PhotoDatabaseHelper;
import com.example.heat_wave.photoviewer.models.Photo;
import com.example.heat_wave.photoviewer.tasks.DownloadImagesTask;
import com.example.heat_wave.photoviewer.tasks.FiveHundredSearchTask;

import java.io.File;
import java.util.ArrayList;

public class ViewerActivity extends Activity
        implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView photoView;
    private PhotoAdapter photoAdapter;
    private RecyclerView.LayoutManager photoLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Bitmap> thumbnailList;
    ProgressBar progressBar;
    PhotoDatabaseHelper db;
    int imagesLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PhotoDatabaseHelper(this);
        setContentView(R.layout.activity_viewer);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        photoView = (RecyclerView) findViewById(R.id.cardList);
        photoView.setHasFixedSize(true);
        photoLayoutManager = new GridLayoutManager(this, 3);
        photoView.setLayoutManager(photoLayoutManager);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setMax(20);
        thumbnailList = new ArrayList<Bitmap>();

        if (!dbExists() || db.getPhotosCount() < 20)
            updatePhotos();
        else {
            for (int i = 1; i <= 20; i++) {
                thumbnailList.add(db.getImageThumbnail(i));
            }
            photoAdapter = new PhotoAdapter(thumbnailList);
            photoAdapter.notifyDataSetChanged();
            photoView.setAdapter(photoAdapter);
        }

        photoView.addItemDecoration(new SpacesItemDecoration(30));
        photoAdapter = new PhotoAdapter(thumbnailList);
        photoView.setAdapter(photoAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSearchFinished(ArrayList<Photo> photos) {
        for (Photo photo : photos) {
            new DownloadImagesTask(this).execute(photo);
        }

    }

    public void onImageDownloaded(Photo photo) {
        db.addPhoto(photo);
        imagesLoaded++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(imagesLoaded);
                if (imagesLoaded == 20) {
                    progressBar.setVisibility(View.INVISIBLE);
                    for (int i = 1; i <= 20; i++) {
                        thumbnailList.add(db.getImageThumbnail(i));
                    }
                    photoAdapter = new PhotoAdapter(thumbnailList);
                    photoAdapter.notifyDataSetChanged();
                    photoView.setAdapter(photoAdapter);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        updatePhotos();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void watchFull(View v) {
        ImageView photo = (ImageView) v;
        Bitmap compare =((BitmapDrawable)photo.getDrawable()).getBitmap();
        int pos = 0;
        for (int i = 0; i < 20; i++) {
            if (compare.equals(thumbnailList.get(i))) {
                pos = i;
                break;
            }
        }
        Intent i = new Intent(this, PhotoSlideActivity.class);
        i.putExtra("POSITION", pos);
        startActivity(i);
    }

    private void updatePhotos() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        imagesLoaded = 0;
        new FiveHundredSearchTask(this).execute();
        Toast.makeText(this, "Updating photos...", Toast.LENGTH_SHORT).show();
    }

    public boolean dbExists() {
        File database = getApplicationContext().getDatabasePath("databasename.db");
        return database.exists();
    }
}
