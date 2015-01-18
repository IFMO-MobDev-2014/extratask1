package com.example.vlad107.extratask1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class ImageLoader extends AsyncTaskLoader<ArrayList<ImageEntry>> {
    Context context;

    public ImageLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList<ImageEntry> loadInBackground() {
        ArrayList<ImageEntry> list = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                DataBaseProvider.IMAGE_URI,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isBeforeFirst() && !cursor.isAfterLast()) {
                byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(ImagesContract.COLUMN_IMAGE));
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ImageEntry img = new ImageEntry(
                        bmp,
                        cursor.getString(cursor.getColumnIndex(ImagesContract.COLUMN_IMAGE_NAME)));
                list.add(img);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
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