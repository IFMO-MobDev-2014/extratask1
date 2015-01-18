package com.android.ilya.extratask1;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class GridActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<Bitmap>> {
    private static final int IMAGES_LOADER_ID = 1;
    List<Bitmap> list;
    GridView grid;
    ImageAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_grid);
        grid = (GridView) findViewById(R.id.gridView2);
        getLoaderManager().initLoader(IMAGES_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        if (list != null) {
            gridAdapter = new ImageAdapter(this, list);
            grid.setAdapter(gridAdapter);
        }
    }

    public Loader<List<Bitmap>> onCreateLoader(int i, Bundle bundle) {
        return new MyImagesListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Bitmap>> listLoader, final List<Bitmap> list) {
        this.list = list;
        update();
    }

    @Override
    public void onLoaderReset(Loader<List<Bitmap>> listLoader) {
        new MyImagesListLoader(this);
    }

}
