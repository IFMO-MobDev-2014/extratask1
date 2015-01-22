package ru.ifmo.md.extratask1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class GridActivity extends ActionBarActivity implements ProgressReceiver.Receiver {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ProgressBar progressBar;
    int progress;
    CustomSwipeRefresh mSwipeRefreshLayout;
    private ProgressReceiver mReceiver;
    static ArrayList<Bitmap> images = new ArrayList<>();

    public static ImageCache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (CustomSwipeRefresh) findViewById(R.id.refresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        cache = new ImageCache(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update(null);
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mReceiver = new ProgressReceiver(new Handler());
        mReceiver.setReceiver(this);

        update(null);
    }


    public void update(View v) {
        images.clear();

        Intent intent = new Intent(this, UpdaterService.class);
        intent.putExtra("receiver", mReceiver);
        startService(intent);
        progress = 0;

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle data) {

        switch (resultCode) {
            case ProgressReceiver.DONE:
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setProgress(0);
                break;
            case ProgressReceiver.ERROR:
                Toast.makeText(this, getString(R.string.conn_error), Toast.LENGTH_SHORT).show();

                break;
            case ProgressReceiver.IMGLOADED:
                //progressBar.setMax(data.getInt("max"));
                progressBar.setProgress(++progress);
                images.add(cache.get(data.getString("url")));
                break;

        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        GridView gridView;
        List<Bitmap> screen = new ArrayList<>();

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int pos = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            gridView = (GridView) rootView.findViewById(R.id.gridView);
            if (!GridActivity.images.isEmpty()) {
                screen = GridActivity.images.subList(6 * (pos - 1), 6 + 6 * (pos - 1));
                gridView.setAdapter(new GridAdapter(getActivity(), screen, 6 * (pos - 1)));
            }

            return rootView;
        }

    }

}
