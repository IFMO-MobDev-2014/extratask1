package ru.ifmo.md.extratask1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

/**
 * Created by anton on 17/01/15.
 */
public class ResultsList extends FragmentActivity {
    static int PAGE_COUNT = 5;
    static int IMAGE_LOADING_TIMEOUT = 4000;

    DbHelper helper;
    ViewPager pager;
    MyFragmentPagerAdapter adapter;
    List<String> allUrls;
    int imagesLoaded;

    Button updateButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);

        // DO gallery = (GridView) findViewById(R.id.gallery);
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

        updateButton = (Button) findViewById(R.id.update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhotos();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress);

        // DO adapter = new ImageAdapter(gallery, this);
        // DO gallery.setAdapter(adapter);

        helper = new DbHelper(this);

        List<String> urls = helper.getUrls();
        if (urls.size() == 0) {
            updatePhotos();
        } else {
            onImageSearchFinished(urls);
        }
    }

    private void updatePhotos() {
        updateButton.setEnabled(false);
        imagesLoaded = 0;
        pager.setAdapter(null);
        ImageSearchTask imSearch = new ImageSearchTask(this);
        imSearch.execute();
    }

    public void onImageSearchFinished(List<String> urls) {
        // DO adapter.setData(urls);
        allUrls = urls;
        helper.setUrls(urls);
        ImageCacher cacher = new ImageCacher(this);
        for (String url : urls) {
            ImageLoadTask loader = new ImageLoadTask(null, cacher, this, url);
            TimeoutTaskRunner.runTask(loader, IMAGE_LOADING_TIMEOUT);
        }
    }

    public void onImageSearchCancelled() {
    }

    public void datasetChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagesLoaded++;
                progressBar.setProgress(100 * imagesLoaded / 50);
                if (imagesLoaded == 50) {
                    updateButton.setEnabled(true);
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
