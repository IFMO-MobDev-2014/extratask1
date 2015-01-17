package com.example.picturemanager;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        Log.d(page + " ", category);
    }

    @Override
    public ArrayList<MyImage> loadInBackground() {
        ArrayList<MyImage> result = new ArrayList<>();
        String selection = DBHelper.PICTURES_CATEGORY + " = \'" + category + "\' and " + DBHelper.PICTURES_PAGE + " = " + page;
        Cursor cursor = getContext().getContentResolver().query(DBContentProvider.PICTURES,
                new String[]{DBHelper.PICTURES_NAME, DBHelper.PICTURES_SMALL_PICTURE,
                        DBHelper.PICTURES_COLUMN_ID}, selection, null, null);
        Log.d("LOADING", "STARTEDHERE!!!!!!!!!!!!!!!!!");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURES_NAME));
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.PICTURES_COLUMN_ID));
            byte[] bArray = cursor.getBlob(cursor.getColumnIndex(DBHelper.PICTURES_SMALL_PICTURE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
            result.add(new MyImage(bitmap, name, id));
            cursor.moveToNext();
            Log.d("NAME", result.get(result.size() - 1).name);
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
