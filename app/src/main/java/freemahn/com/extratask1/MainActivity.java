package freemahn.com.extratask1;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Freemahn on 16.01.2015.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<Entry>> {

    ViewPager pager;
    PagerAdapter pagerAdapter;
    static ArrayList<Entry> entries;

    //ugly button :(
    Button btnRefresh;
    ProgressBar progressBar;
    MyBroadcastReceiver myBroadcastReceiver;
    MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grid_and_pager);
        entries = new ArrayList<>();
        pager = (ViewPager) findViewById(R.id.pager);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Intent intentMyIntentService = new Intent(this, DownloadImagesService.class);
        startService(intentMyIntentService);
        myBroadcastReceiver = new MyBroadcastReceiver();
        myUpdateBroadcastReceiver = new MyUpdateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                DownloadImagesService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter(
                DownloadImagesService.ACTION_UPDATE);
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myUpdateBroadcastReceiver, intentFilter2);

        getLoaderManager().initLoader(0, null, this);
    }

    //OnClick for RefreshBtn
    @Override
    public void onClick(View v) {
        Intent intentMyIntentService = new Intent(this, DownloadImagesService.class);
        startService(intentMyIntentService);

    }

    public Loader<ArrayList<Entry>> onCreateLoader(int i, Bundle bundle) {
        return new ImagesListLoader(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(myUpdateBroadcastReceiver);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Entry>> listLoader, final ArrayList<Entry> list) {
        entries = list;

        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // stub
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Entry>> loader) {
        new ImagesListLoader(this);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setProgress(0);
            getLoaderManager().restartLoader(0, null, MainActivity.this);
        }
    }

    class MyUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(DownloadImagesService.TAG_PERCENT, 0);
            progressBar.setProgress(update);
        }
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
            return entries.size() / 9;
        }

    }



}
