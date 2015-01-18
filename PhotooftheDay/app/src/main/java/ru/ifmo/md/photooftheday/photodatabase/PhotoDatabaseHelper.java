package ru.ifmo.md.photooftheday.photodatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vadim on 18/01/15.
 */
public class PhotoDatabaseHelper extends SQLiteOpenHelper {
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
        db.execSQL("CREATE TABLE " + Tables.PHOTO + "(" +
                        PhotoContract.Photo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PhotoContract.Photo.TITLE + " TEXT NOT NULL, " +
                        PhotoContract.Photo.URL_THUMBNAIL + " TEXT, " +
                        PhotoContract.Photo.URL_FULL + " TEXT, " +
                        PhotoContract.Photo.VALID_STATE + " INTEGER CHECK(" +
                            PhotoContract.Photo.VALID_STATE + " IN (0, 1))" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PhotoDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion +
                        " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PHOTO);
        onCreate(db);
    }
}
