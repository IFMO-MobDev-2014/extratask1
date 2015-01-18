package ru.ifmo.md.extratask1.photoclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import ru.ifmo.md.extratask1.photoclient.database.ImagesProvider;
import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ViewPager mAwesomePager;
    private PagerAdapter mPagerAdapter;

    private SwipeRefreshLayout mRefreshLayout;
    private boolean mIsRefreshing;

    private BroadcastReceiver mUpdateReceiver = new BroadcastStateReceiver();

    private class BroadcastStateReceiver extends BroadcastReceiver {

        public BroadcastStateReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final int stateCode = intent.getIntExtra(BroadcastStateSender.EXTRA_STATE_CODE, BroadcastStateSender.STATE_COMPLETE);
            switch (stateCode) {
                case BroadcastStateSender.STATE_COMPLETE:
                    mIsRefreshing = false;
                    mRefreshLayout.setRefreshing(false);
                    mPagerAdapter.notifyDataSetChanged();
                    break;
                case BroadcastStateSender.STATE_NO_CONNECTION:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    mIsRefreshing = false;
                    mRefreshLayout.setRefreshing(false);
                    break;
                case BroadcastStateSender.STATE_ERROR:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                    mIsRefreshing = false;
                    mRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_view_pager);
        if (!isNetworkAvailable()) {
            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        mAwesomePager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mAwesomePager.setAdapter(mPagerAdapter);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark
        );
    }

    private static final int IMAGES_TO_SHOW = 10;
    private class PagerAdapter extends FragmentStatePagerAdapter {

        private PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int numRows = getNumberOfRows();
            int startIndex = position * IMAGES_TO_SHOW + 1;
            int lastIndex = Math.min(startIndex + IMAGES_TO_SHOW, numRows);
//            Log.d("Tag", "startIndex = " + startIndex);
//            Log.d("Tag", "lastIndex = " + lastIndex);
            return GridPageFragment.newInstance(startIndex, lastIndex);
        }

        @Override
        public int getCount() {
            return (getNumberOfRows() + IMAGES_TO_SHOW - 1) / IMAGES_TO_SHOW;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("TAG", "resume!!!");

        int pagerPosition = mAwesomePager.getCurrentItem();
        mAwesomePager.setAdapter(mPagerAdapter);
        mAwesomePager.setCurrentItem(pagerPosition);

        mRefreshLayout.setRefreshing(mIsRefreshing);

        mUpdateReceiver = new BroadcastStateReceiver();
        IntentFilter intentFilter = new IntentFilter(BroadcastStateSender.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mUpdateReceiver);
            mUpdateReceiver = null;
        }
    }

    @Override
    public void onRefresh() {
        mIsRefreshing = true;
        /*mUpdateReceiver = new BroadcastStateReceiver();
        IntentFilter intentFilter = new IntentFilter(BroadcastStateSender.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mUpdateReceiver, intentFilter);*/
        ImagesLoader.startActionLoadFeed(getApplicationContext());
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    private int getNumberOfRows() {
        Cursor cursor = getContentResolver().query(
                ImagesProvider.CONTENT_URI,
                new String[] { ImagesTable.COLUMN_ID },
                null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }
}
