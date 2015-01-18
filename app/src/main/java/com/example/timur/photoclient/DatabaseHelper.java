package com.example.timur.photoclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by timur on 18.01.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 2;
    public static final String NAME = "photo.db";

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        PhotoTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PhotoTable.onUpgrade(db, oldVersion, newVersion);
    }
}