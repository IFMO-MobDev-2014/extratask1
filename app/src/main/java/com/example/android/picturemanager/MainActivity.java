package com.example.android.picturemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.android.picturemanager.model.MyFeed;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    // TODO if this becomes too memory intensive, it may be best to
    // TODO switch to a {@image_url android.support.v4.app.FragmentStatePagerAdapter}

    private static String[] CATEGORIES;
    private static final int MEMORY_CACHE_SIZE = 10; // MB

    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new UsingFreqLimitedMemoryCache(MEMORY_CACHE_SIZE * 1024 * 1024))
                .denyCacheImageMultipleSizesInMemory()
                .build();

        ImageLoader.getInstance().init(config);

        CATEGORIES = new String[]{
                getString(R.string.category_popular),
                getString(R.string.category_fresh),
                getString(R.string.category_upcoming),
                getString(R.string.category_editors)};

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                actionBar.setTitle(CATEGORIES[position]);
            }
        });

        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_favourite_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_fresh_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_upcoming_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_editors_tab).setTabListener(this));

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            actionBar.setHideOnContentScrollEnabled(true);
        }
        actionBar.setTitle(CATEGORIES[0]);
        actionBar.setLogo(R.drawable.ic_withoutbackground);
        actionBar.setDisplayUseLogoEnabled(true);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            int loc = mViewPager.getCurrentItem();
            ((CategorySectionFragment) getSupportFragmentManager().getFragments().get(loc)).refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    //-------------------------------------------------------------------------------------------

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new CategorySectionFragment();

            Bundle args = new Bundle();
            args.putInt(CategorySectionFragment.SECTION_NUMBER, i);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return CATEGORIES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CATEGORIES[position];
        }
    }

    public static class CategorySectionFragment extends Fragment {
        public static final String SECTION_NUMBER = "section_number";
        private static final int IMAGE_SIZE_SMALL = 2;
        private static final int IMAGE_SIZE_BIG = 4;

        private static int columns;
        private static int imagesPerPage;
        private static int visibleThreshold;
        private static int lastOrientation;

        private ProgressBar progressBar;
        private GridView gridView;
        private int sectionNumber;
        private int pageNumber = 1;
        private MyFeed myFeed;

        private int isServiceWorking = 0;

        private IntentFilter startFilter = new IntentFilter(MyFeed.LOAD_STARTED_BROADCAST);
        private IntentFilter progressFilter = new IntentFilter(MyFeed.PROGRESS_BROADCAST);

        @Override
        public void onPause() {
            super.onPause();
            getActivity().unregisterReceiver(onServiceStart);
            if (isServiceWorking > 0) {
                getActivity().unregisterReceiver(loadingProgress);
                isServiceWorking = 0;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().registerReceiver(onServiceStart, startFilter);
        }

        private BroadcastReceiver onServiceStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (CATEGORIES[sectionNumber].equals(intent.getStringExtra("category"))) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    isServiceWorking = intent.getIntExtra("imagesPerPage", 0);
                    getActivity().registerReceiver(loadingProgress, progressFilter);
                }
            }
        };

        private BroadcastReceiver loadingProgress = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("category").equals(CATEGORIES[sectionNumber])) {
                    progressBar.incrementProgressBy(100 / isServiceWorking);
                    if (progressBar.getProgress() == isServiceWorking * 100 / isServiceWorking) {
                        loadingFinished();
                    }
                }
            }
        };

        private void loadingFinished() {
            progressBar.setVisibility(View.INVISIBLE);
            getActivity().unregisterReceiver(loadingProgress);
            isServiceWorking = 0;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            sectionNumber = args.getInt(SECTION_NUMBER);
        }

        private void initSizes() {
            int orientation = getResources().getConfiguration().orientation;
            if (columns == 0 || orientation != lastOrientation) {
                Point size = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                columns = (int) (size.x / getResources().getDimension(R.dimen.gridView_columnWidth));
                imagesPerPage = (columns * ((int) (size.y
                        / getResources().getDimension(R.dimen.gridView_columnWidth)) + 1));
                visibleThreshold = columns * 4;
                lastOrientation = orientation;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_category, container, false);

            gridView = (GridView) rootView.findViewById(R.id.gridView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            initSizes();

            ImageAdapter imageAdapter = new ImageAdapter(getActivity(), CATEGORIES[sectionNumber]);

            myFeed = new MyFeed(getActivity(), imageAdapter, IMAGE_SIZE_SMALL, IMAGE_SIZE_BIG, CATEGORIES[sectionNumber]);

            gridView.setAdapter(imageAdapter);
            gridView.setNumColumns(columns);

            getActivity().registerReceiver(onServiceStart, startFilter);
            myFeed.loadItems(1, imagesPerPage);

            pageNumber = imagesPerPage / columns;

            gridView.setOnScrollListener(new EndlessScrollListener(visibleThreshold) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    getActivity().registerReceiver(onServiceStart, startFilter);
                    myFeed.loadItems(++pageNumber, visibleThreshold);
                }
            });
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), FullScreenActivityFM.class);
                    intent.putStringArrayListExtra("items", (ArrayList<String>) myFeed.getItems());
                    intent.putExtra("position", position);

                    startActivity(intent);
                }
            });
        }

        public void refresh() {
            myFeed.invalidateData();
            getActivity().registerReceiver(onServiceStart, startFilter);
            myFeed.loadItems(1, imagesPerPage);
            pageNumber = imagesPerPage / columns;
        }
    }
}
