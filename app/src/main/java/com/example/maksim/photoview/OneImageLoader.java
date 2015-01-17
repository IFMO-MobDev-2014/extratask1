package com.example.maksim.photoview;

import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OneImageLoader extends AsyncTaskLoader <Bitmap> {

    ArrayList <Image> images;
    Context context;

    OneImageLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bitmap loadInBackground() {
        Cursor cursor = context.getContentResolver().query(MyContentProvider.IMAGES_CONTENT_URI, null, null, null, null);
        int currentPosition = 0;
        Bitmap largeImage = null;
        if (cursor != null) {
            cursor.moveToFirst();
            while (currentPosition < FullScreenImage.position) {
                cursor.moveToNext();
                currentPosition++;
            }
            byte[] arrayLarge = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.LARGE_IMAGE));
            largeImage = BitmapFactory.decodeByteArray(arrayLarge, 0, arrayLarge.length);
            largeImage = Bitmap.createScaledBitmap(largeImage, 2150, 2500, true);
            cursor.close();
        }
        return largeImage;
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
