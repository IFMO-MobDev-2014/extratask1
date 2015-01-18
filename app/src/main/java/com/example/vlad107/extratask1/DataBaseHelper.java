package com.example.vlad107.extratask1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DataBaseHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "images_app.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_CREATE_SCRIPT_IMAGES  =
            "CREATE TABLE " + ImagesContract.TABLE_NAME
                    + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ImagesContract.COLUMN_IMAGE_NAME + " TEXT NOT NULL, "
                    + ImagesContract.COLUMN_IMAGE + " BLOB);";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImagesContract.TABLE_NAME + ";");
        onCreate(sqLiteDatabase);
    }
}
