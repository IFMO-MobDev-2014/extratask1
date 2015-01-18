package ru.ifmo.md.extratask1;

import android.content.res.Configuration;
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

public class ResultsList extends ActionBarActivity {
    static int IMAGE_LOADING_TIMEOUT = 60 * 1000;

    DbHelper helper;
    ViewPager pager;
    MyFragmentPagerAdapter adapter;
    List<Image> allImages;
    int imagesLoaded;

    MenuItem updateButton;
    ProgressBar progressBar;

    int curPage;
    int pageCount;
    int picsPerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);

        int prevPage = 0;
        int prevPicsPerPage = 0;
        if (savedInstanceState != null) {
            prevPage = savedInstanceState.getInt("curPage");
            prevPicsPerPage = savedInstanceState.getInt("prevPPP");
        }

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            picsPerPage = 10;
            pageCount = 6;
        } else {
            picsPerPage = 6;
            pageCount = 10;
        }
        setCurPage((prevPage + 1) * prevPicsPerPage / picsPerPage);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        helper = new DbHelper(this);

        List<Image> images = helper.getImages();
        if (images.size() == 0) {
            updatePhotos();
        } else {
            onImageSearchFinished(images);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("curPage", curPage);
        savedInstanceState.putInt("prevPPP", picsPerPage);
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

    public void onImageSearchFinished(List<Image> images) {
        allImages = images;
        helper.setImages(images);
        ImageCacher cacher = new ImageCacher(this);
        for (Image image : images) {
            ImageLoadTask loader = new ImageLoadTask(cacher, this, image.url);
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
                progressBar.setProgress(100 * imagesLoaded / 60);
                if (imagesLoaded == 60) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (updateButton != null) {
                        updateButton.setEnabled(true);
                    }
                    pager.setAdapter(adapter);
                    pager.setCurrentItem(curPage);
                }
            }
        });
    }

    public void setCurPage(int page) {
        curPage = page;
        setTitle(getString(R.string.app_name) + " :: " + (page + 1) + " / " + pageCount);
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
            return pageCount;
        }

    }
}
