package com.example.timur.photoclient;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.net.URL;
import java.sql.Blob;

/**
 * Created by timur on 18.01.15.
 */
public class PhotoTable {
    public static final String PHOTOS_TABLE = "photos";

    public static final String AUTHOR = "author";
    public static final String IMAGE_MEDIUM = "img_medium";
    public static final String IMAGE_LARGE = "img_large";
    public static final String ID = "photo_id";
    public static final String IN_FLOW_ID = "photo_flow_id";
    public static final String PHOTOSTREAM_ID = "photostream_id";
    public static final String LARGE_URL = "large_url";
    public static final String BROWSE_URL = "browse_url";
    public static final String PAGE = "photo_page";
    public static final String DESTROY_DATABASE = "drop table if exists " + PHOTOS_TABLE;
    public static final String CREATE_DATABASE = "create table " + PHOTOS_TABLE + " ("
            + "_id integer primary key autoincrement,"
            + AUTHOR + " text,"
            + ID + " text,"
            + LARGE_URL + " text,"
            + IMAGE_MEDIUM + " blob,"
            + IMAGE_LARGE + " blob,"
            + IN_FLOW_ID + " integer,"
            + PHOTOSTREAM_ID + " integer,"
            + BROWSE_URL + " text,"
            + PAGE + " integer" + ");";    
    
    public static void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATABASE);
    }

    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            sqLiteDatabase.execSQL(DESTROY_DATABASE);
            sqLiteDatabase.execSQL(CREATE_DATABASE);
        }
    }
}
