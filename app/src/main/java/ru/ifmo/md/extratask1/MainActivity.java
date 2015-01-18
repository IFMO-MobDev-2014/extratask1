package ru.ifmo.md.extratask1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import ru.ifmo.md.extratask1.api500px.FiveHundredSearchTask;

/**
 * Created by pinguinson on 16.01.2015.
 */
public class MainActivity extends ActionBarActivity {
    public static final int PAGE_COUNT = 6;
    public static final int IMAGES_PER_PAGE = 6;
    public static final int MAX_IMAGES = PAGE_COUNT * IMAGES_PER_PAGE;
    public static final int IMAGE_LOADING_TIMEOUT = 60 * 1000;
    UrlDataBase helper;
    ViewPager pager;
    MyFragmentPagerAdapter adapter;
    List<Photo> allPhotos;
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

        helper = new UrlDataBase(this);

        List<Photo> photos = helper.getUrls();
        if (photos.size() == 0) {
            updatePhotos();
        } else {
            onImageSearchFinished(photos);
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
        int id = item.getItemId();
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
        new FiveHundredSearchTask(this).execute();
    }

    public void onImageSearchFinished(List<Photo> photos) {
        allPhotos = photos;
        helper.setUrls(photos);
        PhotoCacher cacher = new PhotoCacher(this);
        for (Photo photo : photos) {
            PhotoLoadTask loader = new PhotoLoadTask(cacher, this, photo.getFullURL());
            TimeoutTaskRunner.runTask(loader, IMAGE_LOADING_TIMEOUT);
        }
    }

    public void onImageLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagesLoaded++;
                progressBar.setProgress(2 * MAX_IMAGES * imagesLoaded / MAX_IMAGES);
                if (imagesLoaded == MAX_IMAGES) {
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
