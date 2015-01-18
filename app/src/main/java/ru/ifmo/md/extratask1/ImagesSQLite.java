package ru.ifmo.md.extratask1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImagesSQLite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "images_base";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_IMAGES = "images";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PICTURE = "picture";

    private static final String INIT_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + " (" + COLUMN_ID + " INTEGER " + "PRIMARY KEY AUTOINCREMENT, " + COLUMN_PICTURE + " BLOB, " + COLUMN_TITLE + " TEXT, " + COLUMN_AUTHOR + " TEXT );";

    public ImagesSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
