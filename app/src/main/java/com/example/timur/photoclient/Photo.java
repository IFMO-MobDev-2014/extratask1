package com.example.timur.photoclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by timur on 19.01.15.
 */
public class Photo {
    private String author;
    private String browseUrl;
    private String id;
    private int databaseIndex;
    private byte[] image;
    private Bitmap bitmap = null;

    public Photo(String id, String author, byte[] image, String browseUrl, int databaseIndex) {
        this.id = id;
        this.author = author;
        this.image = image;
        this.browseUrl = browseUrl;
        this.databaseIndex = databaseIndex;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            bitmap = BitmapFactory.decodeStream(imageStream);
        }
        return bitmap;
    }

    public String getAuthor() {
        return author;
    }

    public String getBrowseUrl() {
        return browseUrl;
    }

    public String getIndex() {
        return id;
    }

    public int getDatabaseIndex() {
        return databaseIndex;
    }
}