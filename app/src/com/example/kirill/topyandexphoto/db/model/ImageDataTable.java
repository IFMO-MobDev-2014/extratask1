package com.example.kirill.topyandexphoto.db.model;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Kirill on 11.01.2015.
 */
public class ImageDataTable implements BaseColumns {
    public static final String TABLE_NAME = "ImageDataTable";
    public static final String[] sizes = {"S", "M", "L", "XL", "XXL", "XXXL", "orig"};
    public static final String ENTRY_ID_COLUMN = "entryId";
    public static final String ENTRY_URL_COLUMN = "entryUrl";
    public static final String PUBLISHED_COLUMN = "published";
    public static final String TITLE_COLUMN = "title";
    public static final String AUTHOR_NAME_COLUMN = "authorName";
    public static final String PREVIEW_URL_COLUMN = "previewUrl";
    public static final String BIG_URL_COLUMN = "bigUrl";

    private static String DB_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    BaseColumns._ID + " INTEGER PRIMARY KEY autoincrement, " +
                    ENTRY_ID_COLUMN + " text not null, " +
                    ENTRY_URL_COLUMN + " text not null, " +
                    PUBLISHED_COLUMN + " INTEGER , " +
                    TITLE_COLUMN + " text, " +
                    AUTHOR_NAME_COLUMN + " text , " +
                    PREVIEW_URL_COLUMN + " text , " +
                    BIG_URL_COLUMN + " text" + "); ";


     public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
