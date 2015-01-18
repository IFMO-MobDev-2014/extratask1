package com.example.picturemanager;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class ShowPhotoActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<MyImage> {

    ImageView image;
    int photoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        photoId = getIntent().getIntExtra("id", 0);
        image = (ImageView) findViewById(R.id.image);
        getLoaderManager().initLoader(123, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_photo, menu);
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
    public Loader<MyImage> onCreateLoader(int id, Bundle args) {
        return new BigPhotoLoader(this, photoId);
    }

    @Override
    public void onLoadFinished(Loader<MyImage> loader, MyImage data) {
        image.setImageBitmap(data.image);
        getSupportActionBar().setTitle(data.name);
    }

    @Override
    public void onLoaderReset(Loader<MyImage> loader) {
        new BigPhotoLoader(this, photoId);
    }
}
