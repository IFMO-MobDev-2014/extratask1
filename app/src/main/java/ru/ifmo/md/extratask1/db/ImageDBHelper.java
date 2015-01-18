package ru.ifmo.md.extratask1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mikhail on 18.01.15.
 */
public class ImageDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "images";

    public static final String TABLE_LINK = "links";

    public static final String _ID = "_id";
    public static final String COLUMN_NAME_CREATED_AT = "created_at";
    public static final String COLUMN_NAME_FULL_SIZE_LINK = "full_size_link";
    public static final String COLUMN_NAME_MY_ID = "my_id";
    public static final String COLUMN_NAME_PAGE = "page";
    public static final String COLUMN_NAME_FULL_SIZE = "full_size";

    private static final String CREATE_TABLE_LINK = "CREATE TABLE "
            + TABLE_LINK + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_FULL_SIZE_LINK + " TEXT," +
            COLUMN_NAME_MY_ID + " TEXT," +
            COLUMN_NAME_PAGE + " TEXT," +
            COLUMN_NAME_FULL_SIZE + " TEXT," +
            COLUMN_NAME_CREATED_AT + " DATETIME" + " );";

    public ImageDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LINK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINK);

        onCreate(db);
    }
}
