package ru.ifmo.md.extratask1;

import android.graphics.Bitmap;

public class MyImage {
    public Bitmap picture;
    public String author;
    public String title;

    public MyImage(Bitmap picture, String author, String title) {
        this.picture = picture;
        this.author = author;
        this.title = title;
    }
}