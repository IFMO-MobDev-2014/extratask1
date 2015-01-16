package ru.ifmo.md.flickrclient;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.googlecode.flickrjandroid.photos.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sultan on 17.01.15.
 */
public class ImageTask extends AsyncTask<Long, Void, Bitmap> {

    private ImageView imageView;
    private ContentResolver contentResolver;

    public ImageTask(ImageView imageView, ContentResolver contentResolver) {
        this.imageView = imageView;
        this.contentResolver = contentResolver;
    }

    @Override
    protected Bitmap doInBackground(Long... params) {
        Cursor cursor = contentResolver.query(FlickrContentProvider.PHOTO_URI, null, DBFlickr.ID1 + " = ?",
                new String[] {String.valueOf(params[0])}, null);
        Log.d("IMAGE_TASK", String.valueOf(params[0]));
        Photo photo = new Photo();
        cursor.moveToFirst();
        photo.setServer(cursor.getString(cursor.getColumnIndexOrThrow(DBFlickr.PHOTO_SERVER)));
        photo.setSecret(cursor.getString(cursor.getColumnIndexOrThrow(DBFlickr.PHOTO_SECRET)));
        photo.setFarm(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DBFlickr.PHOTO_FARM))));
        photo.setId(cursor.getString(cursor.getColumnIndexOrThrow(DBFlickr.PHOTO_ID)));
        Bitmap bitmap = null;
        String strUrl = photo.getLargeUrl();

        try {
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
