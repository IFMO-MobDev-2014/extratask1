package ru.ifmo.md.extratask1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

public class ResultsList extends ActionBarActivity {
    static int PAGE_COUNT = 5;
    static int IMAGE_LOADING_TIMEOUT = 60 * 1000;

    DbHelper helper;
    ViewPager pager;
    MyFragmentPagerAdapter adapter;
    List<String> allUrls;
    int imagesLoaded;

    MenuItem updateButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);

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

        progressBar = (ProgressBar) findViewById(R.id.progress);

        helper = new DbHelper(this);

        List<String> urls = helper.getUrls();
        Log.i("I have", "" + urls.size() + " urls");
        if (urls.size() == 0) {
            updatePhotos();
        } else {
            onImageSearchFinished(urls);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.results_list, menu);
        updateButton = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updatePhotos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePhotos() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        if (updateButton != null) {
            updateButton.setEnabled(false);
        }
        imagesLoaded = 0;
        pager.setAdapter(null);
        ImageSearchTask imSearch = new ImageSearchTask(this);
        imSearch.execute();
    }

    public void onImageSearchFinished(List<String> urls) {
        allUrls = urls;
        helper.setUrls(urls);
        ImageCacher cacher = new ImageCacher(this);
        for (String url : urls) {
            ImageLoadTask loader = new ImageLoadTask(cacher, this, url);
            TimeoutTaskRunner.runTask(loader, IMAGE_LOADING_TIMEOUT);
        }
    }

    public void onImageSearchCancelled() {
    }

    public void onImageLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagesLoaded++;
                progressBar.setProgress(100 * imagesLoaded / 50);
                if (imagesLoaded == 50) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (updateButton != null) {
                        updateButton.setEnabled(true);
                    }
                    pager.setAdapter(adapter);
                }
            }
        });
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
            return PAGE_COUNT;
        }

    }
}
