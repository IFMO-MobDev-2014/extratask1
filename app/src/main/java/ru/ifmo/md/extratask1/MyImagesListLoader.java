package ru.ifmo.md.extratask1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class MyImagesListLoader extends AsyncTaskLoader<ArrayList<MyImage>> {
    Context context;

    public MyImagesListLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList<MyImage> loadInBackground() {
        ArrayList<MyImage> list = new ArrayList<MyImage>();
        byte[] byteArray;
        MyImage image;
        Cursor cursor = context.getContentResolver().query(ImagesContentProvider.IMAGES_URI, null, null, null, null);
        if (cursor == null) {
            return list;
        }
        cursor.moveToFirst();
        while (!cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            byteArray = cursor.getBlob(cursor.getColumnIndex(ImagesSQLite.COLUMN_PICTURE));
            image = new MyImage(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length), cursor.getString(cursor.getColumnIndex(ImagesSQLite.COLUMN_AUTHOR)), cursor.getString(cursor.getColumnIndex(ImagesSQLite.COLUMN_TITLE)));
            list.add(image);
            cursor.moveToNext();
        }
        cursor.close();
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
