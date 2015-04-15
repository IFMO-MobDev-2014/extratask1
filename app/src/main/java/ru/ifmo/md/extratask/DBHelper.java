package ru.ifmo.md.extratask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gshark on 13.03.15
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String PHOTOS_TABLE_NAME = "photos";

    public static final String COLUMN_ID = "id_";
    public static final String COLUMN_PREVIEW = "preview";
    public static final String COLUMN_LARGE = "large";
    public static final String COLUMN_ORIG = "original";
    public static final String COLUMN_URL = "url";

    private static final String DROP_REQUEST = "DROP TABLE IF EXISTS PHOTOS";

    public static final String CREATE_REQUEST = "CREATE TABLE " + PHOTOS_TABLE_NAME + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ID + " TEXT,"
            + COLUMN_PREVIEW + " TEXT,"
            + COLUMN_LARGE + " TEXT,"
            + COLUMN_ORIG + " TEXT,"
            + COLUMN_URL + " TEXT" + ");";

    public DBHelper(Context context) {
        super(context, PHOTOS_TABLE_NAME, null, 1);
    }

    public void clear(SQLiteDatabase db) {
        db.execSQL(DROP_REQUEST);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clear(db);
    }
}
