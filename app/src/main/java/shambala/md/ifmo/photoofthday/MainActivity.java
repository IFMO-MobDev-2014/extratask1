package shambala.md.ifmo.photoofthday;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ImagesReceiver.Receiver {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    SectionsLandscapePagerAdapter mSectionsLandscapePagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ProgressBar progressBar;
    CustomSwipeRefresh mSwipeRefreshLayout;
    private ImagesReceiver mReceiver;
    static ArrayList<Bitmap> downloaded = new ArrayList<>();
    boolean first = true;
    static boolean[] updated = new boolean[60];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState!=null) {
            first = savedInstanceState.getBoolean("first", true);
        }
        mSwipeRefreshLayout = (CustomSwipeRefresh) findViewById(R.id.refresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(60);
        if (first) {
            for (int i = 0; i < 60; i++) {
                downloaded.add(loadImage(i));
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsLandscapePagerAdapter = new SectionsLandscapePagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mViewPager.setAdapter(mSectionsLandscapePagerAdapter);
        } else {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        mReceiver = new ImagesReceiver(new Handler());
        mReceiver.setReceiver(this);
        if (first) {
            if (downloaded.get(0) == null) {
                Intent intent = new Intent(this, ImageUpdater.class);
                intent.putExtra("receiver", mReceiver);
                startService(intent);
            }
        }



    }


    public void update() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Toast.makeText(this, "Please, wait a moment...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ImageUpdater.class);
        intent.putExtra("receiver", mReceiver);
        startService(intent);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("first", false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        first = savedInstanceState.getBoolean("count");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {

        switch (resultCode) {
            case ImagesReceiver.OK:
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mSectionsLandscapePagerAdapter = new SectionsLandscapePagerAdapter(getSupportFragmentManager());
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mViewPager.setAdapter(mSectionsLandscapePagerAdapter);
                } else {
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setProgress(0);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                break;
            case ImagesReceiver.ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setProgress(0);

                break;
            case  ImagesReceiver.PROGRESS:
                progressBar.setProgress(data.getInt("progress"));
                break;

        }
    }




    public Bitmap loadImage(int id) {
        Bitmap b = null;
        try {
            FileInputStream fis = this.openFileInput(""+id);
            b = BitmapFactory.decodeStream(fis);
            fis.close();


        }
        catch (FileNotFoundException e) {
            Log.d("err", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("err", "io exception");
            e.printStackTrace();
        }
        return b;
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
        int pos;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            fragment.setRetainInstance(true);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            pos = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            gridView = (GridView) rootView.findViewById(R.id.gridView);
            if (!((MainActivity) getActivity()).downloaded.isEmpty()) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    screen = ((MainActivity) getActivity()).downloaded.subList(6 * (pos - 1), 6 + 6 * (pos - 1));
                    gridView.setAdapter(new GridAdapter(getActivity(), screen, 6 * (pos - 1)));
                } else {
                    screen = ((MainActivity) getActivity()).downloaded.subList(10 * (pos - 1), 10 + 10 * (pos - 1));
                    gridView.setAdapter(new GridAdapter(getActivity(), screen, 10 * (pos - 1)));
                }
            }

            return rootView;
        }

    }

}
