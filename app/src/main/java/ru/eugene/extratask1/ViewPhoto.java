package ru.eugene.extratask1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import ru.eugene.extratask1.db.ImageDataSource;
import ru.eugene.extratask1.db.ImageItem;
import ru.eugene.extratask1.db.ImageProvider;
import ru.eugene.extratask1.downloads.DownloadImages;


public class ViewPhoto extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public ImageView photo;
    DownloadImages downloadImages;
    ProgressDialog pd;
    private ImageItem curItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("LOG", "ViewPhoto.onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_photo);
        Intent intent = getIntent();
        curItem = (ImageItem) intent.getSerializableExtra(PhotoActivity.IMAGE_URI);

        Uri uri = Uri.parse(curItem.getThumbnail());
        photo = (ImageView) findViewById(R.id.photo);
        photo.setImageURI(uri);
        photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        pd = new ProgressDialog(this);
        pd.setMessage("Download picture...");
        pd.show();

        downloadImages = new DownloadImages(this, pd);
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    protected void onPause() {
        Log.e("LOG", "ViewPhoto.onPause");
        downloadImages.unRegisterMe();
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("LOG", "ViewPhoto.onConfigurationChanged");
        downloadImages.removeAllServices();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        Log.e("LOG", "ViewPhoto.onResume");
        downloadImages.registerMe();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.wallpaper) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            try {
                photo.buildDrawingCache();
                wallpaperManager.setBitmap(photo.getDrawingCache());
                Toast.makeText(this, "Set wallpaper successfully!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else if (id == R.id.save) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("LOG", "ViewPhoto.onCreateLoader");
        return new CursorLoader(this, ImageProvider.CONTENT_URI_IMAGE, null,
                ImageDataSource.COLUMN_IMAGE_ID + "=?", new String[]{curItem.getImageId()}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("LOG", "ViewPhoto.data.getCount:" + data.getCount());
        if (data == null || data.getCount() == 0) {
            return;
        }
        data.moveToFirst();
        String bigUri = data.getString(data.getColumnIndex(ImageDataSource.COLUMN_BIG_IMAGE));
        Log.e("LOG", "ViewPhoto.bigUri:" + bigUri);
        if (bigUri.isEmpty()) {
            downloadImages.startDownload(curItem);
        } else {
            photo.setImageURI(Uri.parse(bigUri));
            downloadImages.unRegisterMe();
            pd.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("LOG", "onLoaderReset");
        downloadImages.updateImages(null);
    }

}
