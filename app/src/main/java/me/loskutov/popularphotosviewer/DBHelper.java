package me.loskutov.popularphotosviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ignat on 10.11.14.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final String drop = "DROP TABLE IF EXISTS PHOTOS";
    public final String COLUMN_ID = "photoid";
    public final String COLUMN_PREVIEW = "preview";
    public final String COLUMN_LARGE = "large";
    public final String COLUMN_ORIG = "orig";
    public final String COLUMN_URL = "url";
    public DBHelper(Context context) {
        super(context, "PHOTOS", null, 1);
    }

    public void clear(SQLiteDatabase db) {
        Log.d("lal", "CLEAR!!1");
        db.execSQL(drop);
        onCreate(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PHOTOS ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ID + " TEXT,"
                + COLUMN_PREVIEW + " TEXT,"
                + COLUMN_LARGE + " TEXT,"
                + COLUMN_ORIG + " TEXT,"
                + COLUMN_URL + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
