package com.android.ilya.extratask1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "db_image";
    private static final int VERSION = 1;
    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PICTURE = "picture";
    private static final String INIT_IMAGES_TABLE =
            "CREATE TABLE " + TABLE_IMAGES + " (" +
                    COLUMN_ID + " INTEGER " + "PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PICTURE + " BLOB);";

    public MyDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(INIT_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXIST " + TABLE_IMAGES);
        onCreate(db);
    }
}
