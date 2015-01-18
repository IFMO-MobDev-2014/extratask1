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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    ViewPager viewPager;
    MyFragmentPagerAdapter MyFragmentPagerAdapter;
    MyFragmentLandscapePagerAdapter MyFragmentLandscapePagerAdapter;
    static boolean toUpdate = true;

    public Loader<ArrayList<Image>> onCreateLoader(int i, Bundle bundle) {
        return new ImagesAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Image>> listLoader, final ArrayList<Image> list) {
        data = list;
        if (data.isEmpty())
            Toast.makeText(this, getString(R.string.data_is_empty), Toast.LENGTH_LONG).show();
        if (toUpdate) {
            MyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            MyFragmentLandscapePagerAdapter = new MyFragmentLandscapePagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(!portrait ? MyFragmentLandscapePagerAdapter : MyFragmentPagerAdapter);
        }
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
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        MyFragmentLandscapePagerAdapter = new MyFragmentLandscapePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(!portrait ? MyFragmentLandscapePagerAdapter : MyFragmentPagerAdapter);
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
            toUpdate = true;
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

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 8;
        }

    }

    public class MyFragmentLandscapePagerAdapter extends FragmentPagerAdapter {

        public MyFragmentLandscapePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 6;
        }

    }

    public static class MyFragment extends Fragment {
        GridView gridView;
        List<Image> page = new ArrayList<>();
        int number;

        public static MyFragment newInstance(int sectionNumber) {
            MyFragment fragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("page_number", sectionNumber);
            fragment.setArguments(args);
            fragment.setRetainInstance(true);
            return fragment;
        }

        public MyFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            number = getArguments().getInt("page_number");
            View rootView = inflater.inflate(R.layout.page, container, false);
            gridView = (GridView) rootView.findViewById(R.id.gridView);
            final int count;
            if (portrait) {
                count = 6;
                padding = width / 10;
                gridView.setPadding(padding, padding, padding, padding);
                gridView.setVerticalSpacing(padding);
                gridView.setHorizontalSpacing(padding);
                gridView.setNumColumns(2);

            } else {
                count = 8;
                padding = width / 25;
                gridView.setPadding(padding, padding, padding, padding);
                gridView.setVerticalSpacing(padding);
                gridView.setHorizontalSpacing(padding);
                gridView.setNumColumns(4);

            }

            if (!((MainActivity) getActivity()).data.isEmpty()) {
                page = ((MainActivity) getActivity()).data.subList(count * (number - 1), number * count);
                ArrayList<Image> list = new ArrayList<>(page);
                gridView.setAdapter(new GridViewAdapter(getActivity(), list, width, portrait));
            }

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra("POS", count * (number - 1) + position);
                    toUpdate = false;
                    startActivity(intent);
                }
            });
            return rootView;
        }

    }

}
