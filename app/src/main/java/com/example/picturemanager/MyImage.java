package com.example.picturemanager;

import android.graphics.Bitmap;

/**
 * Created by Амир on 17.01.2015.
 */
public class MyImage {
    public Bitmap image;
    public String name;
    public int idInDB;

    public MyImage(Bitmap image, String name, int idInDB) {
        this.image = image;
        this.name = name;
        this.idInDB = idInDB;
    }
}
