package ru.ifmo.md.photooftheday.photodatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vadim on 18/01/15.
 */
public class PhotoDatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = PhotoDatabaseHelper.class.getName();

    public static final String PHOTO_DATABASE_NAME = "photos.db";
    public static final int PHOTO_DATABASE_VERSION = 1;

    public interface Tables {
        public static final String PHOTO = "photo";
    }

    public PhotoDatabaseHelper(Context context) {
        super(context, PHOTO_DATABASE_NAME, null, PHOTO_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createCommand = "CREATE TABLE " /*+ "IF NOT EXISTS "*/ + Tables.PHOTO + "(" +
                PhotoContract.Photo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PhotoContract.Photo.ID + " TEXT NOT NULL, " +
                PhotoContract.Photo.NAME + " TEXT NOT NULL, " +
                PhotoContract.Photo.URL_THUMBNAIL + " TEXT, " +
                PhotoContract.Photo.URL_FULL + " TEXT, " +
                PhotoContract.Photo.VALID_STATE + " INTEGER CHECK(" +
                PhotoContract.Photo.VALID_STATE + " IN (0, 1))" +
                ");";
        Log.d(TAG, "execSQL( " + createCommand + " )");
        db.execSQL(createCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,
                "Upgrading database from version " + oldVersion +
                        " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PHOTO);
        onCreate(db);
    }
}
