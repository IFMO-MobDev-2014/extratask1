package com.example.picturemanager;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final int UPCOMING_CATEGORY = 0;
    public static final int POPULAR_CATEGORY = 1;
    public static final int EDITORS_CATEGORY = 2;

    private int pageNumber;
    private int currentCategory;
    private GridView gridView;
    private ActionBarActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                activity.registerReceiver(receiver, finishFilter);
                startService(currentCategory, pageNumber);
            }
            public void onSwipeRight() {
                activity.registerReceiver(receiver, finishFilter);
                pageNumber++;
                startService(currentCategory, pageNumber);
            }

            public void onSwipeLeft() {
                if (pageNumber != 1) {
                    activity.registerReceiver(receiver, finishFilter);
                    pageNumber--;
                    startService(currentCategory, pageNumber);
                }
            }

            public void onSwipeBottom() {
            }


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        pageNumber = 1;
        currentCategory = POPULAR_CATEGORY;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startService(int category, int pageNumber) {
        Intent intent = new Intent(this, ThumbnailDownloadService.class);
        intent.putExtra("category", category);
        intent.putExtra("pageNumber", pageNumber);
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.upcoming) {
            getSupportActionBar().setTitle(item.getTitle());
            this.registerReceiver(receiver, finishFilter);
            startService(UPCOMING_CATEGORY, 1);
        }
        if (id == R.id.popular) {
            getSupportActionBar().setTitle(item.getTitle());
            this.registerReceiver(receiver, finishFilter);
            startService(POPULAR_CATEGORY, 1);
        }
        if (id == R.id.editors) {
            getSupportActionBar().setTitle(item.getTitle());
            this.registerReceiver(receiver, finishFilter);
            startService(EDITORS_CATEGORY, 1);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    IntentFilter finishFilter = new IntentFilter(ThumbnailDownloadService.LOAD_FINISHED_BROADCAST);
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String category = intent.getStringExtra("category");
            int pageNumber = intent.getIntExtra("pageNumber", 1);
            Log.d("BROADCAST", "CATCHED!");
            activity.getLoaderManager().initLoader(0, null, new MyLoader(pageNumber, category));
            Log.d("LOADER", "STARTED");
            activity.unregisterReceiver(receiver);
        }
    };

    public void setAdapter(ArrayList<MyImage> images) {
        ImageAdapter adapter = new ImageAdapter(this, images);
        gridView.setAdapter(adapter);
    }

    public class MyLoader implements LoaderManager.LoaderCallbacks<ArrayList<MyImage>> {

        int page;
        String category;

        public MyLoader(int page, String category) {
            super();
            Log.d("I", "WAS HERE");
            this.page = page;
            this.category = category;
        }

        @Override
        public Loader<ArrayList<MyImage>> onCreateLoader(int id, Bundle args) {
            Log.d("AND", "HERE");
            return new ImageLoaderAsyncTask(activity, page, category);
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<MyImage>> loader, ArrayList<MyImage> data) {
            Log.d("WAS", "I HERE?");
            setAdapter(data);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MyImage>> loader) {
            new ImageLoaderAsyncTask(activity, page, category);
        }

    }
}

