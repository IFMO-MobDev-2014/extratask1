package com.example.picturemanager;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Амир on 18.01.2015.
 */
public class BigPhotoLoader extends AsyncTaskLoader<MyImage> {

    int photoId;
    Context context;

    public BigPhotoLoader(Context context, int id) {
        super(context);
        this.photoId = id;
        this.context = context;
    }

    @Override
    public MyImage loadInBackground() {
        Log.d("LOADER", "BIGPHOTO");
        Cursor result = getContext().getContentResolver().query(DBContentProvider.PICTURES,
                new String[]{DBHelper.PICTURES_HAS_BIG_PICTURE, DBHelper.PICTURES_NAME, DBHelper.PICTURES_BROWSER_LINK}, DBHelper.PICTURES_COLUMN_ID + " = " + photoId, null, null);
        result.moveToFirst();
        String name = result.getString(result.getColumnIndex(DBHelper.PICTURES_NAME));
        String browserLink = result.getString(result.getColumnIndex(DBHelper.PICTURES_BROWSER_LINK));
        if (result.getInt(result.getColumnIndex(DBHelper.PICTURES_HAS_BIG_PICTURE)) == 1) {
            result = getContext().getContentResolver().query(DBContentProvider.PICTURES,
                    new String[]{DBHelper.PICTURES_BIG_PICTURE}, DBHelper.PICTURES_COLUMN_ID + " = " + photoId, null, null);
            result.moveToFirst();
            byte[] bArray = result.getBlob(result.getColumnIndex(DBHelper.PICTURES_BIG_PICTURE));
            return new MyImage(BitmapFactory.decodeByteArray(bArray, 0, bArray.length), name, photoId, browserLink);
        } else {
            result = getContext().getContentResolver().query(DBContentProvider.PICTURES,
                    new String[]{DBHelper.PICTURES_LINK}, DBHelper.PICTURES_COLUMN_ID + " = " + photoId, null, null);
            result.moveToFirst();
            try {
                URL link = new URL(result.getString(result.getColumnIndex(DBHelper.PICTURES_LINK)));
                Log.d("LINK", result.getString(result.getColumnIndex(DBHelper.PICTURES_LINK)));
                HttpURLConnection connection = (HttpURLConnection) link.openConnection();
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] bArray = outputStream.toByteArray();

                ContentValues cv = new ContentValues();
                cv.put(DBHelper.PICTURES_HAS_BIG_PICTURE, 1);
                cv.put(DBHelper.PICTURES_BIG_PICTURE, bArray);
                getContext().getContentResolver().update(DBContentProvider.PICTURES, cv, DBHelper.PICTURES_COLUMN_ID + " = " + photoId, null);
                return new MyImage(bitmap, name, photoId, browserLink);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
