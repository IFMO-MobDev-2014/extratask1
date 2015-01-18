package com.example.timur.photoclient;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.PhotoList;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PhotosActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static final String API_KEY = "2c0b4b4e1a4d7501b585dd765bd0857f";
    public static final String API_SECRET_KEY = "3cdb99ca3567fb1f";
    private PhotoAdapter myAdapter;
    private GridView gridView;
    private ViewFlipper viewFlipper;
    private Handler handler;
    private int myProgress = 0;
    private int currentPage = 1;
    private boolean updating = false;
    ProgressBar progressBar;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        progressBar = (ProgressBar) findViewById(R.id.progressBarHorizontal);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        intent = new Intent(this, ImageViewActivity.class);
        gridView = (GridView) findViewById(R.id.gridView1);
        List<AnPhoto> list1 = new ArrayList<>();
        myAdapter = new PhotoAdapter(list1);
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(listener);
        getLoaderManager().initLoader(1, null, this);
        if (checkNet()) {
            Intent myIntent = new Intent(this, LoaderService.class);
            myIntent.putExtra(LoaderService.PAGE, currentPage);
            startService(myIntent);
        }

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == 0) {
                    if (myProgress == 0) {
                        updating = true;
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    myProgress++;
                    if (myProgress == 12) {
                        progressBar.setVisibility(View.INVISIBLE);
                        updating = false;
                    }
                    progressBar.setProgress(myProgress);
                } else if (message.what == 1) {
                    progressBar.setVisibility(View.INVISIBLE);
                    updating = false;
                }
                return true;
            }
        });
        LoaderService.setHandler(handler);
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            intent.putExtra(LoaderService.ID, myAdapter.mData.get(position).getId());
            intent.putExtra(LoaderService.DATABASE_ID, myAdapter.mData.get(position).getDbId());
            intent.putExtra(LoaderService.BROUSE, myAdapter.mData.get(position).getBrowseUrl());
            intent.putExtra(LoaderService.TITLE, myAdapter.mData.get(position).getAuthor());
            startActivity(intent);
        }
    };


    private Boolean checkNet() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseContentProvider.PHOTOS_CONTENT_URI, null, PhotoTable.PAGE + " = " + currentPage, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        try {
            if (cursor.getCount() != 0) {
                myAdapter.mData.clear();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    byte[] img = cursor.getBlob(4);
                    String id = cursor.getString(2);
                    AnPhoto photo = new AnPhoto(id, name, img);
                    photo.setDbId(cursor.getInt(0));
                    photo.setBrowseUrl(cursor.getString(8));
                    myAdapter.mData.add(photo);
                }
                myAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myAdapter = new PhotoAdapter(new ArrayList<AnPhoto>());
        gridView.setAdapter(myAdapter);
    }

    public void changePage(int turn) {
        if (turn == 1 || (turn == -1 && currentPage != 1)) {
            myProgress = 0;
            viewFlipper.showPrevious();

            currentPage = currentPage + turn;
            if (currentPage % 2 == 0) {
                gridView = (GridView) findViewById(R.id.gridView2);
            } else {
                gridView = (GridView) findViewById(R.id.gridView1);
            }
            myAdapter = new PhotoAdapter(new ArrayList<AnPhoto>());
            gridView.setAdapter(myAdapter);
            gridView.setOnItemClickListener(listener);
            myAdapter.notifyDataSetChanged();
            getLoaderManager().restartLoader(1, null, PhotosActivity.this);
            if (checkNet()) {
                Intent intent = new Intent(this, LoaderService.class);
                intent.putExtra(LoaderService.PAGE, currentPage);
                startService(intent);
            }
        }
    }

    public void prevPage() {
        changePage(-1);
    }

    public void nextPage() {
        changePage(1);
    }

    public void refresh(View view) {
        if (checkNet() && !updating) {
            Intent myIntent = new Intent(this, LoaderService.class);
            myIntent.putExtra(LoaderService.UPDATE, true);
            myIntent.putExtra(LoaderService.PAGE, currentPage);
            startService(myIntent);
            updating = true;
            myProgress = 0;
        }
    }

    public class PhotoAdapter extends BaseAdapter {

        public List<AnPhoto> mData;

        public PhotoAdapter(List<AnPhoto> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cell = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell, parent, false);
            ImageView imageView = (ImageView) cell.findViewById(R.id.imagepart);
            TextView textView = (TextView) cell.findViewById(R.id.textpart);
            imageView.setImageBitmap(mData.get(position).getBitmap());
            textView.setText("by " + mData.get(position).getAuthor());
            return cell;
        }

    }
}
