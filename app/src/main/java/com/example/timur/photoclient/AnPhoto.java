package com.example.timur.photoclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by timur on 19.01.15.
 */
public class AnPhoto {
    private String author;
    private String browseUrl;
    private String id;
    private int dbId;
    private byte[] image;
    private Bitmap bmp = null;

    public AnPhoto(String id, String author, byte[] image) {
        this.id = id;
        this.author = author;
        this.image = image;
    }

    public Bitmap getBitmap() {
        if (bmp == null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            bmp = BitmapFactory.decodeStream(imageStream);
        }
        return bmp;
    }

    public String getAuthor() {
        return author;
    }

    public String getBrowseUrl() {
        return browseUrl;
    }

    public String getId() {
        return id;
    }

    public int getDbId() {
        return dbId;
    }

    public void setId(String arg) {
        this.id = arg;
    }

    public void setDbId(int arg) {
        this.dbId = arg;
    }

    public void setBrowseUrl(String arg) {
        this.browseUrl = arg;
    }
}