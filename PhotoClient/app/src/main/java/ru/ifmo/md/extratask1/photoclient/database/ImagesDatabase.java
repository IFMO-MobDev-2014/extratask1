package ru.ifmo.md.extratask1.photoclient.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sergey on 10.11.14.
 */
public class ImagesDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "images.db";
    public static final int DB_VERSION = 1;

    public ImagesDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ImagesTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        ImagesTable.onUpgrade(sqLiteDatabase, i, i2);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
