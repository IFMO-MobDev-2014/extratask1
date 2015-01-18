package ru.ifmo.md.extratask1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import ru.ifmo.md.extratask1.db.ImageContentProvider;
import ru.ifmo.md.extratask1.db.ImageDBHelper;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AppResultsReceiver.Receiver {

    public AppResultsReceiver mReceiver;
    ProgressBar mProgressBar;
    ImageCursorAdapter firstImageCursorAdapter;
    ImageCursorAdapter secondImageCursorAdapter;
    ImageCursorAdapter thirdImageCursorAdapter;
    ImageCursorAdapter fourthImageCursorAdapter;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().show();

        if (savedInstanceState != null) {
            count = savedInstanceState.getInt("count");
        }

        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(48);
        mProgressBar.setVisibility(View.GONE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        firstImageCursorAdapter = new ImageCursorAdapter(this, null);
        secondImageCursorAdapter = new ImageCursorAdapter(this, null);
        thirdImageCursorAdapter = new ImageCursorAdapter(this, null);
        fourthImageCursorAdapter = new ImageCursorAdapter(this, null);

        count++;

        if (count == 1) {
            Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
            update();
        }

        getSupportLoaderManager().restartLoader(0, null, this);
        getSupportLoaderManager().restartLoader(1, null, this);
        getSupportLoaderManager().restartLoader(2, null, this);
        getSupportLoaderManager().restartLoader(3, null, this);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", count);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt("count");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                stopService(new Intent(this, ImageSearchAndDownloadService.class));
                Toast.makeText(this, getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
                update();
                break;

            default:
                break;
        }

        return true;
    }

    public ImageCursorAdapter getImageCursorAdapter(int id) {
        if (id == 1) {
            return firstImageCursorAdapter;
        } else if (id == 2) {
            return secondImageCursorAdapter;
        } else if (id == 3) {
            return thirdImageCursorAdapter;
        } else {
            return fourthImageCursorAdapter;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    public void update() {
        Intent intent = new Intent(this, ImageSearchAndDownloadService.class);
        intent.putExtra("receiver", mReceiver);
        this.startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        CursorLoader loader;
        if (id == 0) {
            loader = new CursorLoader(this, ImageContentProvider.LINK_CONTENT_URL, null,
                    ImageDBHelper.COLUMN_NAME_PAGE + "=?", new String[] {"1"}, null);
        } else if (id == 1) {
            loader = new CursorLoader(this, ImageContentProvider.LINK_CONTENT_URL, null,
                    ImageDBHelper.COLUMN_NAME_PAGE + "=?", new String[] {"2"}, null);
        } else if (id == 2) {
            loader = new CursorLoader(this, ImageContentProvider.LINK_CONTENT_URL, null,
                    ImageDBHelper.COLUMN_NAME_PAGE + "=?", new String[] {"3"}, null);
        } else {
            loader = new CursorLoader(this, ImageContentProvider.LINK_CONTENT_URL, null,
                    ImageDBHelper.COLUMN_NAME_PAGE + "=?", new String[] {"4"}, null);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        if (arg0.getId() == 0) {
            firstImageCursorAdapter.swapCursor(cursor);
        } else if (arg0.getId() == 1) {
            secondImageCursorAdapter.swapCursor(cursor);
        } else if (arg0.getId() == 2) {
            thirdImageCursorAdapter.swapCursor(cursor);
        } else {
            fourthImageCursorAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        if (arg0.getId() == 0) {
            firstImageCursorAdapter.swapCursor(null);
        } else if (arg0.getId() == 1) {
            secondImageCursorAdapter.swapCursor(null);
        } else if (arg0.getId() == 2) {
            thirdImageCursorAdapter.swapCursor(null);
        } else {
            fourthImageCursorAdapter.swapCursor(null);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        int position;

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            position = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
            gridView.setAdapter(((MainActivity) getActivity()).getImageCursorAdapter(position));

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case AppResultsReceiver.STATUS_RUNNING :
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(data.getInt("count"));

                break;
            case AppResultsReceiver.STATUS_FINISHED :
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                getSupportLoaderManager().restartLoader(0, null, this);
                getSupportLoaderManager().restartLoader(1, null, this);
                getSupportLoaderManager().restartLoader(2, null, this);
                getSupportLoaderManager().restartLoader(3, null, this);
                Toast.makeText(this, getResources().getString(R.string.images_loaded), Toast.LENGTH_SHORT).show();

                break;
            case AppResultsReceiver.STATUS_INTERNET_ERROR :
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, getResources().getString(R.string.internet_problem), Toast.LENGTH_SHORT).show();

                break;
            case AppResultsReceiver.STATUS_PARSE_ERROR:
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, getResources().getString(R.string.parse_problem), Toast.LENGTH_SHORT).show();

                break;

        }
    }
}
