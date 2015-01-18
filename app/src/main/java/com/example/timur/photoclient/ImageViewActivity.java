package com.example.timur.photoclient;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

    ImageView imageView;
    String photoId;
    String browseUrl;
    AnPhoto thisPhoto;
    int databaseId;

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
            startService(intent);
            return true;
        } else if (id == R.id.action_wallpaper) {
            Intent intent = new Intent(getApplicationContext(), LoaderService.class);
            intent.putExtra(LoaderService.WALLPAPER, true);
            intent.putExtra(LoaderService.DATABASE_ID, databaseId);
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
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            String id = cursor.getString(2);
            byte[] image = cursor.getBlob(5);
            if (image != null) {
                ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                RectF drawableRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
                RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
                Matrix matrix = new Matrix();
                matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageMatrix(matrix);
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();
                thisPhoto = new AnPhoto(id, cursor.getString(1), cursor.getBlob(4));
                thisPhoto.setDbId(cursor.getInt(0));
                thisPhoto.setBrowseUrl(cursor.getString(8));
                browseUrl = thisPhoto.getBrowseUrl();
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
