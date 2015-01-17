package ru.ifmo.md.extratask1.photoclient.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sergey on 15.11.14.
 */
public class ImagesTable {

    public static final String TABLE_NAME = "IMAGES_TABLE";
    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "url_on_web";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_SMALL_CONTENT_URI = "small_content_url";
    public static final String COLUMN_BIG_CONTENT_URI = "big_content_url";

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LINK + " TEXT NOT NULL, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_AUTHOR_NAME  + " TEXT NOT NULL, "
            + COLUMN_SMALL_CONTENT_URI + " TEXT NOT NULL, "
            + COLUMN_BIG_CONTENT_URI + " TEXT NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
