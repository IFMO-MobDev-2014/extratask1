package com.example.maksim.photoview;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class FullScreenImage extends ActionBarActivity implements LoaderManager.LoaderCallbacks <Bitmap> {

    public static int position;
    ImageView view;
    float startX = 0;
    float finalX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen);
        view = (ImageView) findViewById(R.id.imageView);
        position = getIntent().getIntExtra("position", -1);
        getLoaderManager().initLoader(2, null, FullScreenImage.this);
    }

    public void onBackPressed() {
        int current = getIntent().getIntExtra("current", 0);
        Intent intent = new Intent(FullScreenImage.this, MainActivity.class);
        intent.putExtra("current", current);
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                finalX = event.getX();
                if (finalX > startX) {
                    onPrev(view);
                } else {
                    onNext(view);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
        return new OneImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader <Bitmap> listLoader, Bitmap image) {
        view.setImageBitmap(image);
    }

    @Override
    public void onLoaderReset(Loader <Bitmap> listLoader) {}

}
