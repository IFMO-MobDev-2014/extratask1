package ru.ifmo.md.flickrclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sultan on 16.01.15.
 */
public class DBFlickr extends SQLiteOpenHelper {

    private static final String DB_NAME = "photos.db";
    private static final int VERSION = 1;

    public static final String TABLE_PHOTO1 = "photo";
    public static final String ID1 = "_id";
    public static final String PHOTO_ID = "photo_id";
    public static final String PHOTO_FARM = "photo_farm";
    public static final String PHOTO_SERVER = "photo_server";
    public static final String PHOTO_SECRET = "photo_secret";
    public static final String PHOTO = "photo";

    private SQLiteDatabase database;

    private static final String INIT_MONEY1_TABLE =
            "CREATE TABLE " + TABLE_PHOTO1 + " (" +
                    ID1 + " INTEGER " + "PRIMARY KEY AUTOINCREMENT, " +
                    PHOTO_ID + " TEXT, " +
                    PHOTO_FARM + " INTEGER, " +
                    PHOTO_SERVER + " TEXT, " +
                    PHOTO_SECRET + " TEXT, " +
                    PHOTO + " BLOB ) ;";


    public DBFlickr(Context context) {
        super(context, DB_NAME, null, VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(INIT_MONEY1_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO1);
        onCreate(db);
    }
}
