package ru.ifmo.md.extratask;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by gshark on 15.03.15
 */
public class FullResActivity extends FragmentActivity {
    public static final String JPG_FORMAT = ".jpg";

    private ViewPager pager;
    private MyFragmentPagerAdapter pagerAdapter;
    private ArrayList<String> origs;
    private ArrayList<String> ids;
    private ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullres);

        Intent intent = getIntent();

        ArrayList<String> pictures = intent.getStringArrayListExtra(MainActivity.EXTRA_PICTURES);
        ids = intent.getStringArrayListExtra(MainActivity.EXTRA_IDS);
        urls = intent.getStringArrayListExtra(MainActivity.EXTRA_URLS);
        origs = intent.getStringArrayListExtra(MainActivity.EXTRA_ORIGS);
        int position = intent.getIntExtra(MainActivity.EXTRA_POSITION, 0);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), pictures, ids);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fullres, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_set_wallpaper) {
            (new WallpaperSetTask(
                    getApplicationContext(),
                    pagerAdapter.fragments.get(pager.getCurrentItem()).getBitmap()
            )).execute();
            return true;
        } else if (id == R.id.action_download) {
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            int ind = pager.getCurrentItem();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(origs.get(ind)));
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, ids.get(ind) + JPG_FORMAT);
            dm.enqueue(request);
            return true;
        } else if (id == R.id.action_open_browser) {
            final Intent intent = new Intent(
                    Intent.ACTION_VIEW, Uri.parse(urls.get(pager.getCurrentItem())));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<String> pictures;
        private final ArrayList<String> ids;
        public final ArrayList<PagerFragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<String> pictures, ArrayList<String> ids) {
            super(fm);
            this.pictures = pictures;
            this.ids = ids;
            fragments = new ArrayList<>(ids.size());
            for (int i = 0; i < ids.size(); i++) {
                fragments.add(null);
            }
        }

        @Override
        public Fragment getItem(int position) {
            PagerFragment fragment = PagerFragment.newInstance(position, pictures.get(position), ids.get(position));
            fragments.set(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 50;
        }

    }
}
