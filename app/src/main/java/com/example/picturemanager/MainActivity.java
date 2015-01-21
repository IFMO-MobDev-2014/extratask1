package com.example.picturemanager;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final String UPCOMING_CATEGORY = "upcoming";
    public static final String POPULAR_CATEGORY = "popular";
    public static final String EDITORS_CATEGORY = "editors";


    private int pageNumber;
    private String currentCategory;
    private GridView gridView;
    private MainActivity activity;
    private TextView pageNumberText;
    private ProgressBar progressBar;
    private boolean isServiceWorking;
    private ArrayList<MyImage> images;
    private IntentFilter startFilter = new IntentFilter(ThumbnailDownloadService.LOAD_STARTED_BROADCAST);
    private IntentFilter finishFilter = new IntentFilter(ThumbnailDownloadService.LOAD_FINISHED_BROADCAST);
    private IntentFilter progressFilter = new IntentFilter(ThumbnailDownloadService.PROGRESS_BROADCAST);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                if (!isServiceWorking) {
                    pageNumber++;
                    updatePageAndTitle();
                    loadPhotos(currentCategory, pageNumber);
                }
            }

            public void onSwipeRight() {
                if (!isServiceWorking && pageNumber != 1) {
                    pageNumber--;
                    updatePageAndTitle();
                    loadPhotos(currentCategory, pageNumber);
                }
            }


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isServiceWorking) {
                    Intent intent = new Intent(MainActivity.this, ShowPhotoActivity.class);
                    intent.putExtra("id", images.get(position).idInDB);
                    startActivity(intent);
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pageNumberText = (TextView) findViewById(R.id.numPage);
        pageNumber = 1;
        currentCategory = POPULAR_CATEGORY;
        registerReceiver(onServiceStart, startFilter);
        updatePageAndTitle();
        loadPhotos(currentCategory, pageNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void loadPhotos(String category, int pageNumber) {
        getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, category));
    }

    public void updatePageAndTitle() {
        getSupportActionBar().setTitle(currentCategory);
        pageNumberText.setText("Page " + pageNumber);
    }

    //MainActivity --Done
    //Icon --Done
    //refresh --Done
    //wallpaper save etc
    //isOnline --Done

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (!isServiceWorking && id == R.id.refresh) {
            Intent start = new Intent();
            start.setAction(ThumbnailDownloadService.LOAD_STARTED_BROADCAST);
            sendBroadcast(start);
            Intent intent = new Intent(this, ThumbnailDownloadService.class);
            intent.putExtra("category", currentCategory);
            intent.putExtra("pageNumber", pageNumber);
            startService(intent);
        }
        if (!isServiceWorking && id == R.id.upcoming) {
            pageNumber = 1;
            currentCategory = UPCOMING_CATEGORY;
            updatePageAndTitle();
            loadPhotos(UPCOMING_CATEGORY, 1);

        }
        if (!isServiceWorking && id == R.id.popular) {
            pageNumber = 1;
            currentCategory = POPULAR_CATEGORY;
            updatePageAndTitle();
            loadPhotos(POPULAR_CATEGORY, 1);
        }
        if (!isServiceWorking && id == R.id.editors) {
            pageNumber = 1;
            currentCategory = EDITORS_CATEGORY;
            updatePageAndTitle();
            loadPhotos(EDITORS_CATEGORY, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(onServiceStart);
        if (isServiceWorking) {
            unregisterReceiver(onServiceFinish);
            unregisterReceiver(loadingProgress);
            isServiceWorking = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(onServiceStart, startFilter);
    }



    public BroadcastReceiver onServiceStart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                isServiceWorking = true;
                activity.registerReceiver(onServiceFinish, finishFilter);
                activity.registerReceiver(loadingProgress, progressFilter);
            } else {
                Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public BroadcastReceiver onServiceFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String category = intent.getStringExtra("category");
            progressBar.setVisibility(View.INVISIBLE);
            int pageNumber = intent.getIntExtra("pageNumber", 1);
            activity.getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, category));
            activity.unregisterReceiver(onServiceFinish);
            activity.unregisterReceiver(loadingProgress);
            isServiceWorking = false;
        }
    };

    public BroadcastReceiver loadingProgress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            progressBar.setProgress(progress*100/ThumbnailDownloadService.imagesPerPage);
        }
    };


    public void setAdapter(ArrayList<MyImage> images) {
        this.images = images;
        ImageAdapter imageAdapter = new ImageAdapter(this, images);

        gridView.setAdapter(imageAdapter);
    }

    public class MyLoader implements LoaderManager.LoaderCallbacks<ArrayList<MyImage>> {

        int page;
        String category;

        public MyLoader(int page, String category) {
            super();
            this.page = page;
            this.category = category;
        }

        @Override
        public Loader<ArrayList<MyImage>> onCreateLoader(int id, Bundle args) {
            return new ImageLoaderAsyncTask(activity, page, category);
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<MyImage>> loader, ArrayList<MyImage> data) {
            setAdapter(data);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MyImage>> loader) {
            new ImageLoaderAsyncTask(activity, page, category);
        }

    }
}

