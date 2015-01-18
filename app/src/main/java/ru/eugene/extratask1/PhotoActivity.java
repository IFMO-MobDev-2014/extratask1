package ru.eugene.extratask1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ru.eugene.extratask1.db.ImageDataSource;
import ru.eugene.extratask1.db.ImageItem;
import ru.eugene.extratask1.db.ImageProvider;
import ru.eugene.extratask1.downloads.DownloadImages;
import ru.eugene.extratask1.downloads.DownloadService;

public class PhotoActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String IMAGE_URI = "image_uri";
    private ProgressDialog pd;
    private GridView gridview;
    private ImageAdapter gridAdapter;
    private DownloadImages downloadImages;
    private Context context;
    private long curPage = 0;
    private long maxPage = 100000;

    public long getCntItems() {
        Cursor tempCursor = getContentResolver().query(ImageProvider.CONTENT_URI_IMAGE, null, null, null, null);
        long res = tempCursor.getCount();
        tempCursor.close();
        return res;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
//        Log.e("LOG", "onCreate");

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadImages = new DownloadImages(this, pd);

        gridAdapter = new ImageAdapter(this, R.layout.image_layout, downloadImages.getImages());
        gridview = (GridView) findViewById(R.id.gridview);

        gridview.setAdapter(gridAdapter);
        context = this;
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewPhoto = new Intent(context, ViewPhoto.class);
                viewPhoto.putExtra(PhotoActivity.IMAGE_URI, downloadImages.getImages().get(position));
                context.startActivity(viewPhoto);
            }
        });

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putLong("curPage", curPage);
        // etc.
        super.onSaveInstanceState(savedInstanceState);
    }

    //onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        curPage = savedInstanceState.getLong("curPage");
        updInterface();
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    protected void onPause() {
//        Log.e("LOG", "onPause");
        downloadImages.unRegisterMe();
        downloadImages.removeAllServices();
        super.onPause();
    }

    @Override
    protected void onResume() {
//        Log.e("LOG", "onResume");
        downloadImages.registerMe();
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        Log.e("LOG", "onConfigurationChanged");
        downloadImages.removeAllServices();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ImageProvider.CONTENT_URI_IMAGE, null, null, null, ImageDataSource.COLUMN_ID +
                " LIMIT " + DownloadImages.CNT_IMAGES_ON_PAGE + " OFFSET " + curPage * DownloadImages.CNT_IMAGES_ON_PAGE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getCntItems();
        downloadImages.updateImages(data);
        if (data == null || data.getCount() == 0) {
            if (!pd.isShowing()) {
                pd = new ProgressDialog(this);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setIndeterminate(true);
                pd.setProgress(0);
                pd.show();
                downloadImages.setPd(pd);
            }
            downloadImages.startDownload();
            return;
        }

        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("LOG", "onLoaderReset");
        downloadImages.updateImages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setIndeterminate(true);
            pd.setProgress(0);
            pd.show();
            getLoaderManager().restartLoader(1, null, this);
            downloadImages.setPd(pd);
            downloadImages.startDownload();
            return true;
        } else if (id == R.id.action_cancel) {
            curPage = 0;
            updInterface();
            getContentResolver().delete(ImageProvider.CONTENT_URI_IMAGE, "_id>0", null);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPrev(View v) {
        if (curPage == 0) return;
        curPage--;
        updInterface();
        getLoaderManager().restartLoader(1, null, this);
    }

    public void onNext(View v) {
        if ((curPage + 1) * DownloadImages.CNT_IMAGES_ON_PAGE > getCntItems()) return;
        curPage++;
        updInterface();
        getLoaderManager().restartLoader(1, null, this);
    }

    public void updInterface() {
        ((TextView) findViewById(R.id.curPage)).setText("page: " + (curPage + 1));
    }
}
