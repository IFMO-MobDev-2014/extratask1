package com.example.picturemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Амир on 14.01.2015.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;

    public static final String PICTURES_TABLE_NAME = "PICTURES";
    public static final String PICTURES_COLUMN_ID = "ID";
    public static final String PICTURES_CATEGORY = "CATEGORY";
    public static final String PICTURES_PAGE = "PAGE";
    public static final String PICTURES_NAME = "NAME";
    public static final String PICTURES_SMALL_PICTURE = "SMALL_PICTURE";
    public static final String PICTURES_HAS_BIG_PICTURE = "HAS_BIG_PICTURE";
    public static final String PICTURES_BIG_PICTURE = "BIG_PICTURE";
    public static final String PICTURES_LINK = "LINK";
    public static final String PICTURES_ON_CREATE = "create table if not exists " + PICTURES_TABLE_NAME + "("
            + PICTURES_COLUMN_ID + " integer primary key autoincrement, " + PICTURES_CATEGORY + " text, "
            + PICTURES_PAGE + " integer, " + PICTURES_NAME + " text, " + PICTURES_SMALL_PICTURE + " blob, "
            + PICTURES_HAS_BIG_PICTURE + " boolean, " + PICTURES_BIG_PICTURE + " blob, " + PICTURES_LINK + " text);";
    public static final String PICTURES_ON_DESTROY = "drop table if exists " + PICTURES_TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PICTURES_ON_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(PICTURES_ON_DESTROY);
        onCreate(sqLiteDatabase);
    }
}
