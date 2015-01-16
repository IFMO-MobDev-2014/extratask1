package com.photofinder;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class ImagesAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Image>> {
    Context context;

    public ImagesAsyncTaskLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList<Image> loadInBackground() {
        ArrayList<Image> imageArrayList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MyContentProvider.URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isBeforeFirst() && !cursor.isAfterLast()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.KEY_PIC));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Image image = new Image(bitmap, cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_LINK)), cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_XXL_LINK)));
                imageArrayList.add(image);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return imageArrayList;
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