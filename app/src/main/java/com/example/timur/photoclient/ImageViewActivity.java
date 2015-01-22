package com.example.timur.photoclient;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;


public class ImageViewActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView imageView;
    private String photoId;
    private String browseUrl;
    private Photo thisPhoto;
    private int databaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        photoId = getIntent().getStringExtra(LoaderService.ID);
        databaseId = getIntent().getIntExtra(LoaderService.DATABASE_ID, 0);
        browseUrl = getIntent().getStringExtra(LoaderService.BROUSE);
        this.setTitle(getIntent().getStringExtra(LoaderService.TITLE));

        imageView = (ImageView) findViewById(R.id.fullscreen_content);
        getLoaderManager().initLoader(1, null, this);
        if (checkInternetConnection()) {
            Intent servIntent = new Intent(this, LoaderService.class);
            servIntent.putExtra(LoaderService.ID, photoId);
            servIntent.putExtra(LoaderService.DATABASE_ID, databaseId);
            servIntent.setAction(LoaderService.ACTION_DOWNLOAD_PHOTO);
            startService(servIntent);
        }
    }

    public boolean checkInternetConnection() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            Intent intent = new Intent(getApplicationContext(), LoaderService.class);
            intent.putExtra(LoaderService.WALLPAPER, true);
            intent.putExtra(LoaderService.SAVE, true);
            intent.putExtra(LoaderService.DATABASE_ID, databaseId);
            intent.setAction(LoaderService.ACTION_SAVE);
            startService(intent);
            return true;
        } else if (id == R.id.action_wallpaper) {
            Intent intent = new Intent(getApplicationContext(), LoaderService.class);
            intent.putExtra(LoaderService.WALLPAPER, true);
            intent.putExtra(LoaderService.DATABASE_ID, databaseId);
            intent.setAction(LoaderService.ACTION_SET_WALLPAPER);
            startService(intent);
            return true;
        } else if (id == R.id.action_browse) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(browseUrl));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ContentUris.withAppendedId(DatabaseContentProvider.PHOTOS_CONTENT_URI, databaseId);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() != 0 && !cursor.isLast()) {
            try {
                cursor.moveToNext();
                String id = cursor.getString(6);
                byte[] image = cursor.getBlob(5);
                if (image != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(image));
                    Matrix matrix = new Matrix();
                    matrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                            new RectF(0, 0, imageView.getWidth(), imageView.getHeight()),
                            Matrix.ScaleToFit.CENTER);
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
                    imageView.setImageMatrix(matrix);
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                    thisPhoto = new Photo(id, cursor.getString(3), cursor.getBlob(2), cursor.getString(8), cursor.getInt(0));
                    browseUrl = thisPhoto.getBrowseUrl();
                }
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
