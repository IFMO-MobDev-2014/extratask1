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
import android.widget.AdapterView;
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
    private boolean isServiceWorking;
    private ArrayList<MyImage> images;
    IntentFilter finishFilter = new IntentFilter(ThumbnailDownloadService.LOAD_FINISHED_BROADCAST);

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
                if (!isServiceWorking) {
                    pageNumber++;
                    updatePageNumberText();
                    startService(currentCategory, pageNumber);
                }
            }

            public void onSwipeRight() {
                if (!isServiceWorking && pageNumber != 1) {
                    pageNumber--;
                    updatePageNumberText();
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ShowPhotoActivity.class);
                intent.putExtra("id", images.get(position).idInDB);
                startActivity(intent);

            }
        });
        pageNumberText = (TextView) findViewById(R.id.numPage);
        pageNumber = 1;
        currentCategory = POPULAR_CATEGORY;
        getSupportActionBar().setTitle("Popular");
        getLoaderManager().initLoader(0, null, new MyLoader(pageNumber, currentCategory));
        startService(currentCategory, pageNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startService(String category, int pageNumber) {
        isServiceWorking = true;
        activity.registerReceiver(receiver, finishFilter);
        getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, category));
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
        if (!isServiceWorking && id == R.id.upcoming) {
            getSupportActionBar().setTitle(item.getTitle());
            pageNumber = 1;
            updatePageNumberText();
            startService(UPCOMING_CATEGORY, 1);

        }
        if (!isServiceWorking && id == R.id.popular) {
            getSupportActionBar().setTitle(item.getTitle());
            pageNumber = 1;
            updatePageNumberText();
            startService(POPULAR_CATEGORY, 1);
        }
        if (!isServiceWorking && id == R.id.editors) {
            getSupportActionBar().setTitle(item.getTitle());
            pageNumber = 1;
            updatePageNumberText();
            startService(EDITORS_CATEGORY, 1);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String category = intent.getStringExtra("category");
            int pageNumber = intent.getIntExtra("pageNumber", 1);
            Log.d(category, pageNumber + " ");
            activity.unregisterReceiver(receiver);
            activity.getLoaderManager().restartLoader(0, null, new MyLoader(pageNumber, category));
            isServiceWorking = false;
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

