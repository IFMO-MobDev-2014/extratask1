package com.android.ilya.extratask1;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<Bitmap>> {
    private static final int IMAGES_LOADER_ID = 0;
    GridView gridView;
    ViewPager pager;
    MyBroadcastReceiver myBroadcastReceiver;
    MyFragmentPagerAdapter adapter;
    ProgressBar progressBar;
    List<Bitmap> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        pager = (ViewPager) findViewById(R.id.pager);
                adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

                        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    }

                                        @Override
                                public void onPageSelected(int position) {
                               }

                               @Override
                               public void onPageScrollStateChanged(int state) {
                               }
                           });

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadImageTask.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        getLoaderManager().initLoader(IMAGES_LOADER_ID, null, this);
    }

    public int getCurrentPage() {
        return pager.getCurrentItem();
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
            if (isOnline()) startService(new Intent(this, DownloadImageTask.class));
            else Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    public Loader<List<Bitmap>> onCreateLoader(int i, Bundle bundle) {
        return new MyImagesListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Bitmap>> listLoader, final List<Bitmap> list) {
        images = list;
        if (images.size() == 0) {
            if (isOnline()) startService(new Intent(this, DownloadImageTask.class));
            else Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        } else {
            //gridView.setAdapter(new ImageAdapter(this, list));
            pager.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Bitmap>> listLoader) {
        new MyImagesListLoader(this);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(DownloadImageTask.TAG_PERCENT, -1);
            if (progress != -100) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setProgress(0);
                    getLoaderManager().restartLoader(IMAGES_LOADER_ID, null, MainActivity.this);
                }
            } else {
                getLoaderManager().restartLoader(IMAGES_LOADER_ID, null, MainActivity.this);
            }
        }

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable() && netInfo.isConnected();
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
                super(fm);
            }

                @Override
        public Fragment getItem(int position) {
                return PageFragment.newInstance(position);
            }

                @Override
        public int getCount() {
                return 4;
            }

    }
}
