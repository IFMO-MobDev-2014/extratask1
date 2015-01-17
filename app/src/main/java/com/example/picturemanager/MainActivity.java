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
    private ActionBarActivity activity;
    private TextView pageNumberText;

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
                activity.getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, currentCategory));

            }
            public void onSwipeLeft() {
                activity.registerReceiver(receiver, finishFilter);
                pageNumber++;
                updatePageNumberText();
                startService(currentCategory, pageNumber);
                activity.getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, currentCategory));

            }

            public void onSwipeRight() {
                if (pageNumber != 1) {
                    activity.registerReceiver(receiver, finishFilter);
                    pageNumber--;
                    updatePageNumberText();
                    startService(currentCategory, pageNumber);
                    activity.getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, currentCategory));
                }
            }

            public void onSwipeBottom() {
            }


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        pageNumberText = (TextView) findViewById(R.id.numPage);
        pageNumber = 1;
        currentCategory = POPULAR_CATEGORY;
        getLoaderManager().initLoader(0, null, new MyLoader(pageNumber, "popular"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startService(String category, int pageNumber) {
        Intent intent = new Intent(this, ThumbnailDownloadService.class);
        intent.putExtra("category", category);
        intent.putExtra("pageNumber", pageNumber);
        startService(intent);
    }

    public void updatePageNumberText() {
        pageNumberText.setText("Page " + pageNumber);
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
            pageNumber = 1;
            updatePageNumberText();
            getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, currentCategory));
            startService(UPCOMING_CATEGORY, 1);

        }
        if (id == R.id.popular) {
            getSupportActionBar().setTitle(item.getTitle());
            this.registerReceiver(receiver, finishFilter);
            pageNumber = 1;
            updatePageNumberText();
            getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, currentCategory));
            startService(POPULAR_CATEGORY, 1);
        }
        if (id == R.id.editors) {
            getSupportActionBar().setTitle(item.getTitle());
            this.registerReceiver(receiver, finishFilter);
            pageNumber = 1;
            updatePageNumberText();
            getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, currentCategory));
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
            Log.d(category, pageNumber + " ");
            activity.getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, category));
            activity.unregisterReceiver(receiver);
        }
    };

    public void setAdapter(ArrayList<MyImage> images) {
        ImageAdapter adapter = new ImageAdapter(this, images);
        Log.d("UPDATED", "WOOOOOOW");

        gridView.setAdapter(adapter);
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

