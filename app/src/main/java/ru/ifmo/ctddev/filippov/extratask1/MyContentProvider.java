package ru.ifmo.ctddev.filippov.extratask1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dima_2 on 01.03.2015.
 */
public class MyContentProvider extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "my_database";
    public static final String PHOTOS_TABLE = "photos_table";
    public static final Integer VERSION = 5;

    public static final String PHOTO_KEY_AUTHOR = "author";
    public static final String PHOTO_KEY_IMAGE_MEDIUM = "image_medium";
    public static final String PHOTO_KEY_IMAGE_LARGE = "image_large";
    public static final String PHOTO_KEY_ID = "id";
    public static final String PHOTO_KEY_IN_FLOW_ID = "in_flow_id";
    public static final String PHOTO_KEY_PHOTOSTREAM_ID = "photostream_id";
    public static final String PHOTO_KEY_LARGE_URL = "large_url";
    public static final String PHOTO_KEY_BROWSE_URL = "browse_url";
    public static final String PHOTO_KEY_PAGE = "page";


    public MyContentProvider(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + PHOTOS_TABLE + " ("
                + "_id integer primary key autoincrement,"
                + PHOTO_KEY_AUTHOR + " text,"
                + PHOTO_KEY_ID + " text,"
                + PHOTO_KEY_LARGE_URL + " text,"
                + PHOTO_KEY_IMAGE_MEDIUM + " blob,"
                + PHOTO_KEY_IMAGE_LARGE + " blob,"
                + PHOTO_KEY_IN_FLOW_ID + " integer,"
                + PHOTO_KEY_PHOTOSTREAM_ID + " integer,"
                + PHOTO_KEY_BROWSE_URL + " text,"
                + PHOTO_KEY_PAGE + " integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("drop table if exists " + PHOTOS_TABLE);
        onCreate(database);
    }
}