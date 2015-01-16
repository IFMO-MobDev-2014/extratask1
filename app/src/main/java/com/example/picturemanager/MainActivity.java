package com.example.picturemanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements  LoaderManager.LoaderCallbacks<Cursor> {

    public static final int UPCOMING_CATEGORY = 0;
    public static final int POPULAR_CATEGORY = 1;
    public static final int EDITORS_CATEGORY = 2;

    private int pageNumber;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
            }


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        pageNumber = 1;

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
            startService(UPCOMING_CATEGORY, 1);
        }
        if (id == R.id.popular) {
            getSupportActionBar().setTitle(item.getTitle());
            startService(POPULAR_CATEGORY, 1);
        }
        if (id == R.id.editors) {
            getSupportActionBar().setTitle(item.getTitle());
            startService(EDITORS_CATEGORY, 1);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String[] projection = {DBHelper.CHANNELS_COLUMN_NAME, DBHelper.CHANNELS_COLUMN_LINK, DBHelper.CHANNELS_COLUMN_ID};
        //return new CursorLoader(this, DBContentProvider.CHANNELS, projection, null, null, null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
