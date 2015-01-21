package com.example.picturemanager;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Амир on 17.01.2015.
 */
public class ImageLoaderAsyncTask extends AsyncTaskLoader<ArrayList<MyImage>> {

    String category;
    int page;

    public ImageLoaderAsyncTask(Context context, int page, String category) {
        super(context);
        this.page = page;
        this.category = category;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public ArrayList<MyImage> loadInBackground() {
        ArrayList<MyImage> result = new ArrayList<>();
        String selection = DBHelper.PICTURES_CATEGORY + " = \'" + category + "\' and " + DBHelper.PICTURES_PAGE + " = " + page;
        Cursor cursor = getContext().getContentResolver().query(DBContentProvider.PICTURES,
                new String[]{DBHelper.PICTURES_NAME, DBHelper.PICTURES_SMALL_PICTURE,
                        DBHelper.PICTURES_COLUMN_ID, DBHelper.PICTURES_BROWSER_LINK}, selection, null, null);
        if (cursor.getCount() != ThumbnailDownloadService.imagesPerPage) {
            Intent start = new Intent();
            start.setAction(ThumbnailDownloadService.LOAD_STARTED_BROADCAST);
            if (isNetworkAvailable()) {
                Intent intent = new Intent(getContext(), ThumbnailDownloadService.class);
                intent.putExtra("category", category);
                intent.putExtra("pageNumber", page);
                getContext().startService(intent);
                start.putExtra("isNetworkAvailable", true);
            } else {
                start.putExtra("isNetworkAvailable", false);
            }
            getContext().sendBroadcast(start);
        } else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURES_NAME));
                String browserLink = cursor.getString((cursor.getColumnIndex(DBHelper.PICTURES_BROWSER_LINK)));
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.PICTURES_COLUMN_ID));
                byte[] bArray = cursor.getBlob(cursor.getColumnIndex(DBHelper.PICTURES_SMALL_PICTURE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
                result.add(new MyImage(bitmap, name, id, browserLink));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return result;
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
