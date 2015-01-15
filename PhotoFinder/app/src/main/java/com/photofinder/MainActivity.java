package com.photofinder;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
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

public class MainActivity extends ActionBarActivity {
    ProgressBar progressBar;
    ViewPager viewPager;
    MySwipeRefresh MySwipeRefresh;
    ArrayList<Bitmap> bitmaps;
    ArrayList<Pair<String, String>> links;
    static int width;
    static int padding;
    static boolean portrait;
    private DataBaseAdapter dataBaseAdapter;
    private MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;
    private MyBroadcastReceiver myBroadcastReceiver;
    IntentFilter intentFilter;
    IntentFilter intentFilterUpdate;
    FragmentPagerAdapterPortrait fragmentPagerAdapterPortrait;
    FragmentPagerAdapterLandscape fragmentPagerAdapterLandscape;



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
        dataBaseAdapter = new DataBaseAdapter(this);
        dataBaseAdapter.open();
        bitmaps = dataBaseAdapter.getAllPics();
        links = dataBaseAdapter.getAllLinks();
        myUpdateBroadcastReceiver = new MyUpdateBroadcastReceiver();
        intentFilterUpdate = new IntentFilter(ImageService.ACTION_UPDATE);
        intentFilterUpdate.addCategory(Intent.CATEGORY_DEFAULT);
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter(ImageService.ACTION_READY);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myUpdateBroadcastReceiver, intentFilterUpdate);
        registerReceiver(myBroadcastReceiver, intentFilter);
        fragmentPagerAdapterPortrait = new FragmentPagerAdapterPortrait(getSupportFragmentManager());
        fragmentPagerAdapterLandscape = new FragmentPagerAdapterLandscape(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewPager.setAdapter(fragmentPagerAdapterLandscape);
        } else {
            viewPager.setAdapter(fragmentPagerAdapterPortrait);
        }

        MySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MainActivity.this, ImageService.class);
                startService(intent);
            }
        });

    }

    public class FragmentPagerAdapterLandscape extends FragmentPagerAdapter {


        public FragmentPagerAdapterLandscape(FragmentManager fragmentManager) {
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

    public class FragmentPagerAdapterPortrait extends FragmentPagerAdapter {
        public FragmentPagerAdapterPortrait(FragmentManager fragmentManager) {
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
            fragmentPagerAdapterPortrait = new FragmentPagerAdapterPortrait(getSupportFragmentManager());
            fragmentPagerAdapterLandscape = new FragmentPagerAdapterLandscape(getSupportFragmentManager());
            viewPager.setAdapter(!portrait ? fragmentPagerAdapterLandscape : fragmentPagerAdapterPortrait);
            Bitmap update = intent.getParcelableExtra(ImageService.EXTRA_KEY_UPDATE);
            int id = intent.getIntExtra(ImageService.EXTRA_KEY_PROGRESS, 0);
            String link = intent.getStringExtra(ImageService.EXTRA_KEY_LINK);
            String xxlLink = intent.getStringExtra(ImageService.EXTRA_KEY_XXL_LINK);
            progressBar.setProgress(id + 1);
            if (bitmaps.size() <= id) {
                bitmaps.add(update);
                links.add(new Pair<>(link, xxlLink));
            } else {
                bitmaps.set(id, update);
                links.set(id, new Pair<>(link, xxlLink));
            }
            if (id == getResources().getInteger(R.integer.pictures_count) - 1)
                progressBar.setProgress(0);
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra(ImageService.EXTRA_KEY_SUCCESS, false)) {
                Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                MySwipeRefresh.setRefreshing(false);
                progressBar.setProgress(0);
                return;
            }
            processNewPics(bitmaps, links);
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


    private void processNewPics(ArrayList<Bitmap> bitmap, ArrayList<Pair<String, String>> link) {
        if (bitmap == null) {
            bitmap = dataBaseAdapter.getAllPics();
            link = dataBaseAdapter.getAllLinks();

        }
        if (bitmap != null && bitmap.size() == getResources().getInteger(R.integer.pictures_count)) {
            dataBaseAdapter.deletePics();
            for (int i = 0; i < getResources().getInteger(R.integer.pictures_count); ++i) {
                dataBaseAdapter.addPic(bitmap.get(i), link.get(i));
            }
        }
        fragmentPagerAdapterPortrait = new FragmentPagerAdapterPortrait(getSupportFragmentManager());
        fragmentPagerAdapterLandscape = new FragmentPagerAdapterLandscape(getSupportFragmentManager());
        viewPager.setAdapter(!portrait ? fragmentPagerAdapterLandscape : fragmentPagerAdapterPortrait);
    }


    public static class MyFragment extends Fragment {



        public static MyFragment newInstance(int sectionNumber) {
            MyFragment fragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("section_number", sectionNumber);
            fragment.setArguments(args);
            fragment.setRetainInstance(true);
            return fragment;
        }

        public MyFragment() {
        }
        List<Bitmap> page;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
            final int pos = getArguments().getInt("section_number");
            int count = 6;
            if (portrait) {
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
            if ( ((MainActivity) getActivity()).bitmaps.size() < getResources().getInteger(R.integer.pictures_count))
                return rootView;
            page = ((MainActivity) getActivity()).bitmaps.subList(count * (pos - 1), count * pos);
            final int finalCount = count;
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra("POS", finalCount * (pos - 1) + position);
                    startActivity(intent);
                }
            });
            ArrayList<Bitmap> tmp = new ArrayList<>(page);
            gridView.setAdapter(new GridViewAdapter(getActivity(), tmp, width, portrait));
            return rootView;
        }

    }
}
