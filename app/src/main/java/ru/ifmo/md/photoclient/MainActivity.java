package ru.ifmo.md.photoclient;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Шолохов on 16.01.2015.
 */

public class MainActivity extends FragmentActivity
        implements  MyResultReceiver.Receiver {

    final static int photoLimit = 90;
    final static int landscapeImagesLimit = 15;
    final static int portraitImagesLimit = 15;
    final static int landscapeColumns = 5;
    final static int portraitColumns = 3;
    final static String requestInteresting = "http://api-fotki.yandex.ru/api/recent/updated/?limit=" + photoLimit;

    MyResultReceiver receiver;
    ProgressBar pBar;
    public static ArrayList<Bitmap> data;

    MySwipePagerAdapter msHorisontalPagerAdapter;
    MySwipePagerAdapter msVerticalPagerAdapter;

    ViewPager msViewPager;
    android.support.v4.widget.SwipeRefreshLayout msRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new MyResultReceiver(new Handler());
        receiver.setReceiver(this);

        msViewPager = (ViewPager) findViewById(R.id.view_pager);
        pBar = (ProgressBar)findViewById(R.id.general_progress_bar);
        msRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.main_layout);

        msRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromInternet(requestInteresting);
            }
        });

        pBar.setMax(photoLimit);


        data = new ArrayList<>();
        loadDataFromDisk();
        if (data.isEmpty()) {
            Log.d("DEBUG", "Loading started");
            loadDataFromInternet(requestInteresting);
        }


        msHorisontalPagerAdapter = new MySwipePagerAdapter(getSupportFragmentManager(), photoLimit/ landscapeImagesLimit);
        msVerticalPagerAdapter = new MySwipePagerAdapter(getSupportFragmentManager(), photoLimit/ portraitImagesLimit);


        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                msViewPager.setAdapter(msVerticalPagerAdapter);

            case Configuration.ORIENTATION_LANDSCAPE:
                msViewPager.setAdapter(msHorisontalPagerAdapter);

        }
    }

    void loadDataFromDisk() {
        for (int i = 1; i <= photoLimit; i++) {
            try {
                FileInputStream fis = this.openFileInput(""+i);
                Bitmap b = BitmapFactory.decodeStream(fis);
                data.add(b);
                fis.close();
            } catch (Exception e) {
                Log.d("ERROR", "on Loading");
            }
        }
    }

    void loadDataFromInternet(String link) {
        Intent myImagesLoader = new Intent(MainActivity.this, MyIntentService.class);
        myImagesLoader.putExtra("link", link);
        myImagesLoader.putExtra("receiver", receiver);
        myImagesLoader.putExtra("limit", photoLimit);
        myImagesLoader.putExtra("mode", 1);
        startService(myImagesLoader);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        if (resultCode == MyResultReceiver.FAIL) {
            Log.d("ERROR", "fail from intent service");
            msRefreshLayout.setRefreshing(false);
            pBar.setProgress(0);
        }
        else if (resultCode == MyResultReceiver.PROGRESS) {
            int progress = data.getInt("count");
            pBar.setProgress(progress);
        }
        else if (resultCode == MyResultReceiver.DONE) {
            loadDataFromDisk();
            msHorisontalPagerAdapter = new MySwipePagerAdapter(getSupportFragmentManager(), photoLimit/ landscapeImagesLimit);
            msVerticalPagerAdapter = new MySwipePagerAdapter(getSupportFragmentManager(), photoLimit/ portraitImagesLimit);
            switch (getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT: {
                    msViewPager.setAdapter(msVerticalPagerAdapter);
                    break;
                }
                case Configuration.ORIENTATION_LANDSCAPE: {
                    msViewPager.setAdapter(msHorisontalPagerAdapter);
                    break;
                }
            }
            pBar.setProgress(0);
            msRefreshLayout.setRefreshing(false);
        }
    }

    public static class TabFragment extends android.support.v4.app.Fragment {

        MainActivity activity;
        GridView gridView;
        View rootView;
        TextView pageNumberTextView;
        List<Bitmap> oneTabImages;


        public static TabFragment newTab(int i, int j) {
            TabFragment tf = new TabFragment();
            Bundle params = new Bundle();
            params.putInt("number", i);
            params.putInt("total", j);
            tf.setArguments(params);
            tf.setRetainInstance(true);
            return tf;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final int pageNumber = getArguments().getInt("number");
            final int totalPages = getArguments().getInt("total");

            activity = (MainActivity)getActivity();
            rootView = inflater.inflate(R.layout.single_page, container, false);
            gridView = (GridView)rootView.findViewById(R.id.img_preview_gridview);
            pageNumberTextView = (TextView)rootView.findViewById(R.id.page_number_textview);
            pageNumberTextView.setText((pageNumber+1) + " / " + totalPages);

            if (!activity.data.isEmpty()) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    oneTabImages = activity.data.subList(portraitImagesLimit * (pageNumber), portraitImagesLimit + portraitImagesLimit * (pageNumber));
                    MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(), oneTabImages, portraitImagesLimit * (pageNumber));
                    gridView.setNumColumns(portraitColumns);
                    gridView.setAdapter(myGridAdapter);

                } else {
                    oneTabImages = activity.data.subList(landscapeImagesLimit * (pageNumber), landscapeImagesLimit + landscapeImagesLimit * (pageNumber));
                    MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(), oneTabImages, landscapeImagesLimit * (pageNumber));
                    gridView.setNumColumns(landscapeColumns);
                    gridView.setAdapter(myGridAdapter);
                }
            }
            return rootView;
        }
    }

}
