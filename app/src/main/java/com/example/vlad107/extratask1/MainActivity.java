package com.example.vlad107.extratask1;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<ImageEntry>> {

    ProgressDialog curProgress;
    GridView gridView;
    ImageReceiver imageReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.grid_view);

        curProgress = new ProgressDialog(this);
        curProgress.setCancelable(false);

        imageReceiver = new ImageReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(imageReceiver, intentFilter);

        getLoaderManager().initLoader(107, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            startService(new Intent(this, DownloadService.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<ImageEntry>> onCreateLoader(int i, Bundle bundle) {
        return new ImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ImageEntry>> arrayListLoader, ArrayList<ImageEntry> imageEntries) {
        gridView.setAdapter(new ImageAdapter(this, imageEntries));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putInt(ViewActivity.APP_PREFERENCES_POSITION, position);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ImageEntry>> arrayListLoader) {
    }

    public class ImageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(DownloadService.PERCENT, -1);
            curProgress.setProgress(progress);
            curProgress.setMessage(String.valueOf(progress) + "% downloaded");
            curProgress.show();
            if (progress == 100) {
                curProgress.dismiss();
                getLoaderManager().restartLoader(107, null, MainActivity.this);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imageReceiver);
    }
}
