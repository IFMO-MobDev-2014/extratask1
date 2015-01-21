package com.example.picturemanager;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


public class ShowPhotoActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<MyImage> {

    private ImageView imageView;
    private MyImage image;
    private int photoId;
    boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        photoId = getIntent().getIntExtra("id", 0);
        imageView = (ImageView) findViewById(R.id.image);
        if (isNetworkAvailable()) {
            getLoaderManager().initLoader(123, null, this);
        } else {
            Toast.makeText(this, getString(R.string.noInternetConnection), Toast.LENGTH_SHORT).show();
        }
        loaded = false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!loaded) {
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_set_as_walpaper) {
            Intent intent = new Intent(this, LocalActionsService.class);
            intent.putExtra("id", image.idInDB);
            intent.setAction("set as wallpaper");
            startService(intent);
        }
        if (id == R.id.action_save) {
            Intent intent = new Intent(this, LocalActionsService.class);
            intent.putExtra("id", image.idInDB);
            intent.setAction("save");
            startService(intent);
        }
        if (id == R.id.action_open_in_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.baseURL) + image.link));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<MyImage> onCreateLoader(int id, Bundle args) {
        return new BigPhotoLoader(this, photoId);
    }

    @Override
    public void onLoadFinished(Loader<MyImage> loader, MyImage data) {
        imageView.setImageBitmap(data.image);
        getSupportActionBar().setTitle(data.name);
        image = data;
        loaded = true;
    }

    @Override
    public void onLoaderReset(Loader<MyImage> loader) {
        new BigPhotoLoader(this, photoId);
    }
}
