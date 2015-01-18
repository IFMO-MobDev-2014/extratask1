package com.photofinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "pics";

    public static final String TABLE_PICS = "pictures";
    public static final String KEY_ID = "_id";
    public static final String KEY_PIC = "pic";
    public static final String KEY_LINK = "link";
    public static final String KEY_XXL_LINK = "xxllink";


    private static final String INIT_PICS = "CREATE TABLE " + TABLE_PICS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_PIC + " BLOB, " + KEY_LINK + " TEXT, " + KEY_XXL_LINK + " TEXT)";

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(INIT_PICS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICS);
        onCreate(db);
    }

}