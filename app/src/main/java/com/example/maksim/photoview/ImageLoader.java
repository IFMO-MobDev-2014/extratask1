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

public class ImageLoader extends AsyncTaskLoader <List <Image>>{

    ArrayList <Image> images;
    Context context;

    ImageLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public List <Image> loadInBackground() {
        images = new ArrayList<Image>();
        Cursor cursor = context.getContentResolver().query(MyContentProvider.IMAGES_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                byte[] arraySmall = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.SMALL_IMAGE));
                byte[] arrayLarge = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.LARGE_IMAGE));
                Bitmap smallImage = BitmapFactory.decodeByteArray(arraySmall, 0, arraySmall.length);
                //Bitmap largeImage = BitmapFactory.decodeByteArray(arrayLarge, 0, arrayLarge.length);
                String largeImageLink = cursor.getString(cursor.getColumnIndex(SQLiteHelper.LARGE_IMAGE));
                Bitmap largeImage = smallImage;
                // TODO: normal work with large images
                images.add(new Image(smallImage, largeImage, largeImageLink));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return images;
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
