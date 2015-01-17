package ru.ifmo.md.flickrclient;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.photos.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sultan on 17.01.15.
 */
public class ImageTask extends AsyncTask<Long, Void, Bitmap> {

    public static final String ROW_ID = "ROW_ID";
    private ContentResolver contentResolver;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Toast toast;


    public ImageTask(ContentResolver contentResolver, ImageView imageView, ProgressDialog progressDialog, Toast toast) {
        this.contentResolver = contentResolver;
        this.imageView = imageView;
        this.progressDialog = progressDialog;
        this.toast = toast;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Downloading image");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        try {
            progressDialog.cancel();
            if (bitmap == null) {
                toast.show();
            } else {
                imageView.setImageBitmap(bitmap);
                toast.cancel();
            }
        } catch (IllegalArgumentException e) {
        }
    }

    @Override
    protected Bitmap doInBackground(Long... params) {
        long rowId = params[0];
        if (rowId == -1) {
            return null;
        }
        Cursor cursor = contentResolver.query(FlickrContentProvider.PHOTO_URI, null, DBFlickr.ID1 + " = ?",
                new String[] {String.valueOf(rowId)}, null);
        Log.d("IMAGE_TASK", String.valueOf(rowId));
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
        return  bitmap;
    }

}
