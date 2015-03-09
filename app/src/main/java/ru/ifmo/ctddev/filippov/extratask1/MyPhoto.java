package ru.ifmo.ctddev.filippov.extratask1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by Dima_2 on 01.03.2015.
 */
public class MyPhoto {
    String author;
    String id;
    byte[] image;
    private Bitmap bitmap = null;
    int databaseId;
    String fullUrl;
    String browseUrl;

    public MyPhoto(String id, String author, byte[] image) {
        this.id = id;
        this.author = author;
        this.image = image;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            bitmap = BitmapFactory.decodeStream(imageStream);
        }
        return bitmap;
    }

}
