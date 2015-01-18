package com.android.ilya.extratask1;

/**
 * Created by Ilya on 16.01.2015.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class MyImagesListLoader extends AsyncTaskLoader<List<Bitmap>> {
    Context context;

    public MyImagesListLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<Bitmap> loadInBackground() {
        List<Bitmap> list = new ArrayList<Bitmap>();

        Cursor c = context.getContentResolver().query(ImageContentProvider.IMAGES_URI, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
            while (!c.isBeforeFirst() && !c.isAfterLast())  {
                byte[] byteArray = c.getBlob(c.getColumnIndex(MyDatabase.COLUMN_PICTURE));
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                list.add(bmp);
                c.moveToNext();
            }
        }
        if (c != null) {
            c.close();
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

