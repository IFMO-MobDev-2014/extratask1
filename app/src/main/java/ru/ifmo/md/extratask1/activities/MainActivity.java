package ru.ifmo.md.extratask1.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import ru.ifmo.md.extratask1.PageFragment;
import ru.ifmo.md.extratask1.Photo;
import ru.ifmo.md.extratask1.storage.LinksDataBase;
import ru.ifmo.md.extratask1.storage.PhotoCacher;
import ru.ifmo.md.extratask1.loading.PhotoLoadTask;
import ru.ifmo.md.extratask1.R;
import ru.ifmo.md.extratask1.loading.TimeoutTaskRunner;
import ru.ifmo.md.extratask1.api500px.FiveHundredSearchTask;

/**
 * Created by pinguinson on 16.01.2015.
 */
public class MainActivity extends ActionBarActivity {
    public static final int PAGE_COUNT = 6;
    public static final int IMAGES_PER_PAGE = 6;
    public static final int MAX_IMAGES = PAGE_COUNT * IMAGES_PER_PAGE;
    public static final int IMAGE_LOADING_TIMEOUT = 60 * 1000;
    public List<Photo> allPhotos;
    LinksDataBase helper;
    ViewPager pager;
    MyFragmentPagerAdapter adapter;
    int imagesLoaded;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updatePhotos();
            }
        });

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
        progressBar.setMax(MAX_IMAGES);
        helper = new LinksDataBase(this);
        List<Photo> photos = helper.getUrls();
        if (photos.size() == 0) {
            updatePhotos();
        } else {
            onImageSearchFinished(photos);
        }
    }

    private void updatePhotos() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        imagesLoaded = 0;
        new FiveHundredSearchTask(this).execute();
        Toast.makeText(this, "Updating photos...", Toast.LENGTH_SHORT).show();
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

    public void onPhotoLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagesLoaded++;
                progressBar.setProgress(imagesLoaded);
                if (imagesLoaded == MAX_IMAGES) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.INVISIBLE);
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
