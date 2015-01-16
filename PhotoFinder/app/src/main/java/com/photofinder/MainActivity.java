package com.photofinder;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<ArrayList<Image>> {
    ProgressBar progressBar;
    MySwipeRefresh MySwipeRefresh;
    ArrayList<Image> data = new ArrayList<>();
    static int width;
    static int padding;
    static boolean portrait;
    private MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;
    private MyBroadcastReceiver myBroadcastReceiver;
    IntentFilter intentFilter;
    IntentFilter intentFilterUpdate;
    GridView gridView;
    public Loader<ArrayList<Image>> onCreateLoader(int i, Bundle bundle) {
        return new ImagesAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Image>> listLoader, final ArrayList<Image> list) {
        data = list;
        gridView.setAdapter(new GridViewAdapter(this, data, width, portrait));
        if (data.isEmpty())
            Toast.makeText(this, getString(R.string.data_is_empty), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<Image>> listLoader) {
        new ImagesAsyncTaskLoader(this);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MySwipeRefresh = (MySwipeRefresh) findViewById(R.id.refresh);
        MySwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(getResources().getInteger(R.integer.pictures_count));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        getLoaderManager().initLoader(0, null, this);
        myUpdateBroadcastReceiver = new MyUpdateBroadcastReceiver();
        intentFilterUpdate = new IntentFilter(ImageService.ACTION_UPDATE);
        intentFilterUpdate.addCategory(Intent.CATEGORY_DEFAULT);
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter(ImageService.ACTION_READY);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myUpdateBroadcastReceiver, intentFilterUpdate);
        registerReceiver(myBroadcastReceiver, intentFilter);
        gridView = (GridView) findViewById(R.id.gridView);
        if (portrait) {
            padding = width / 10;
            gridView.setPadding(padding, padding, padding, padding);
            gridView.setVerticalSpacing(padding);
            gridView.setHorizontalSpacing(padding);
            gridView.setNumColumns(2);

        } else {
            padding = width / 25;
            gridView.setPadding(padding, padding, padding, padding);
            gridView.setVerticalSpacing(padding);
            gridView.setHorizontalSpacing(padding);
            gridView.setNumColumns(4);

        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("POS", position);
                startActivity(intent);
            }
        });

        gridView.setAdapter(new GridViewAdapter(this, data, width, portrait));
        MySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MainActivity.this, ImageService.class);
                startService(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myUpdateBroadcastReceiver, intentFilterUpdate);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myUpdateBroadcastReceiver);
        unregisterReceiver(myBroadcastReceiver);
    }


    public class MyUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MySwipeRefresh.setRefreshing(false);
            int id = intent.getIntExtra(ImageService.EXTRA_KEY_PROGRESS, 0);
            progressBar.setProgress(id + 1);
            if (id == getResources().getInteger(R.integer.pictures_count) - 1)
                progressBar.setProgress(0);
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra(ImageService.EXTRA_KEY_SUCCESS, false)) {
                Toast.makeText(MainActivity.this, getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                MySwipeRefresh.setRefreshing(false);
                progressBar.setProgress(0);
                return;
            }
            getLoaderManager().restartLoader(0, null, MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
