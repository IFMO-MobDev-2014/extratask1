package com.example.maksim.photoview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME =  "dataBase";
    public static final String IMAGE_TABLE = "images";
    public static final String COLUMN_ID = "_id";
    public static final String SMALL_IMAGE = "smallImage";
    public static final String LARGE_IMAGE = "largeImage";
    //public static final String LINK_ON_LARGE = "link";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_IMAGES_BASE = "create table " + IMAGE_TABLE + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + LARGE_IMAGE + " TEXT, "
            + SMALL_IMAGE + " BLOB" + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(CREATE_IMAGES_BASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        dataBase.execSQL("drop table if exists " + IMAGE_TABLE);
        onCreate(dataBase);
    }
}
