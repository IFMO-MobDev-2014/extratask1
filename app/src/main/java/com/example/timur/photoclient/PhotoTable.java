package com.example.timur.photoclient;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by timur on 18.01.15.
 */
public class PhotoTable {
    public static final String NAME = "photos";
    public static final String AUTHOR = "author";
    public static final String IMAGE_MEDIUM = "image_medium";
    public static final String IMAGE_LARGE = "image_large";
    public static final String ID = "photo_id";
    public static final String IN_FLOW_ID = "photo_flow_id";
    public static final String PHOTO_STREAM_ID = "photo_stream_id";
    public static final String LARGE_URL = "large_url";
    public static final String BROWSE_URL = "browse_url";
    public static final String PAGE = "photo_page";
    public static final String DESTROY_DATABASE = "drop table if exists " + NAME;
    public static final String CREATE_DATABASE = "create table " + NAME
            + " ("
            + "_id integer primary key autoincrement,"
            + AUTHOR + " text,"
            + ID + " text,"
            + LARGE_URL + " text,"
            + IMAGE_MEDIUM + " blob,"
            + IMAGE_LARGE + " blob,"
            + IN_FLOW_ID + " integer,"
            + PHOTO_STREAM_ID + " integer,"
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
