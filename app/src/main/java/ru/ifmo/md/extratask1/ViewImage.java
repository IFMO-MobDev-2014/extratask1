package ru.ifmo.md.extratask1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.ifmo.md.extratask1.db.ImageContentProvider;
import ru.ifmo.md.extratask1.db.ImageDBHelper;


public class ViewImage extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AppResultsReceiver.Receiver {
    String myId;
    String fullSizeLink;
    String page;
    public AppResultsReceiver mReceiver;
    ProgressBar mProgressBar;
    int count = 0;
    boolean fullAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        if (savedInstanceState != null) {
            count = savedInstanceState.getInt("count");
            fullAvailable = savedInstanceState.getBoolean("full_available");
        }

        getSupportActionBar().hide();

        myId = getIntent().getStringExtra("my_id");
        fullSizeLink = getIntent().getStringExtra("full_size_link");
        page = getIntent().getStringExtra("page");

        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);

        count++;

        if (count == 1) {
            getSupportLoaderManager().initLoader(0, null, this);
        } else {
            ImageView imageView = (ImageView) findViewById(R.id.image2);
            imageView.setImageBitmap(loadImageFromInternalStorage(this, myId));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        CursorLoader loader;
            loader = new CursorLoader(this, ImageContentProvider.LINK_CONTENT_URL, null, ImageDBHelper.COLUMN_NAME_MY_ID + "=?", new String[] {myId}, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String fullSize = cursor.getString(cursor.getColumnIndex("full_size"));
                if (fullSize.equals("yes")) {
                    ImageView imageView = (ImageView) findViewById(R.id.image2);
                    imageView.setImageBitmap(loadImageFromInternalStorage(this, myId));
                } else {
                    Intent intent = new Intent(this, FullImageService.class);
                    intent.putExtra("my_id", myId);
                    intent.putExtra("full_size_link", fullSizeLink);
                    intent.putExtra("page", page);
                    intent.putExtra("receiver", mReceiver);
                    this.startService(intent);
                }
                cursor.close();
            } else {
                fullAvailable = false;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", count);
        outState.putBoolean("full_available", fullAvailable);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt("count");
        fullAvailable = savedInstanceState.getBoolean("full_available");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case AppResultsReceiver.STATUS_RUNNING :
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(50);

                break;
            case AppResultsReceiver.STATUS_FINISHED :
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                getSupportLoaderManager().restartLoader(0, null, this);
                ImageView imageView = (ImageView) findViewById(R.id.image2);
                imageView.setImageBitmap(loadImageFromInternalStorage(this, myId));
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

    public Bitmap loadImageFromInternalStorage(Context context, String id) {
        Bitmap bitmap = null;
        try {
            FileInputStream fis = context.openFileInput("f" + id);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
