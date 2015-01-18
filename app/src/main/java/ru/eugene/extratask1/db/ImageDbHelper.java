package ru.eugene.extratask1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by eugene on 1/16/15.
 */
class ImageDbHelper extends SQLiteOpenHelper {
    private final static String NAME = "image_data_base";
    public static final int VERSION = 17;

    public ImageDbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("LOG", "ImageDbHelper.onCreate()");
        db.execSQL(ImageDataSource.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ImageDataSource.TABLE_NAME);
        onCreate(db);
    }
}
