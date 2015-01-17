package com.example.maksim.photoview;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImage extends ActionBarActivity implements LoaderManager.LoaderCallbacks <Bitmap> {

    //ProgressDialog dialog;

    public static int position;
    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen);
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
        view = (ImageView) findViewById(R.id.imageView);
        //Bitmap largeImage = getIntent().getParcelableExtra("image");
        //view.setImageBitmap(largeImage);
        //String link = getIntent().getStringExtra("link");
        position = getIntent().getIntExtra("position", -1);
        getLoaderManager().initLoader(2, null, FullScreenImage.this);
        /*try {
            URL url = new URL(link);
            url.openConnection();
            InputStream is = url.openConnection().getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);
            view.setImageBitmap(image);
            is.close();
        } catch(Exception e) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("No Internet Connection");
            dialog.show();
        }*/
    }

    public void onBackPressed() {
        Intent intent = new Intent(FullScreenImage.this, MainActivity.class);
        startActivity(intent);
    }

    public void onPrev(View view) {
        position--;
        if (position < 0) {
            position = 99;
        }
        getLoaderManager().restartLoader(2, null ,FullScreenImage.this);
    }

    public void onNext(View view) {
        position++;
        if (position > 99) {
            position = 0;
        }
        getLoaderManager().restartLoader(2, null, FullScreenImage.this);
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
        //return new ImageLoader(this);\
        return new OneImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader <Bitmap> listLoader, Bitmap image) {
        view.setImageBitmap(image);
    }

    @Override
    public void onLoaderReset(Loader <Bitmap> listLoader) {}

}
