package ru.eugene.extratask1.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by eugene on 1/17/15.
 */
public class ImageProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.eugene.extratask1.db";
    public static final String IMAGE_PATH = "image";
    public static final Uri CONTENT_URI_IMAGE = Uri.parse("content://" + AUTHORITY + "/" + IMAGE_PATH);

    private ImageDbHelper db;

    @Override
    public boolean onCreate() {
        Log.e("LOG", "ContentProvider.onCreate()");
        db = new ImageDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Log.e("LOG", "ContentProvider.query");
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor result = database.query(ImageDataSource.TABLE_NAME, projection,
                selection, selectionArgs, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = db.getWritableDatabase();
        long id = database.insert(ImageDataSource.TABLE_NAME, null, values);
        if (id > 0) {
            uri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(uri, null);
        }
//        database.close();
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int id = database.delete(ImageDataSource.TABLE_NAME, selection, selectionArgs);
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        database.close();
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int id = database.update(ImageDataSource.TABLE_NAME, values, selection, selectionArgs);
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        database.close();
        return id;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase database = db.getWritableDatabase();
        int cnt = 0;
        for (ContentValues value : values) {
            long id = database.insert(ImageDataSource.TABLE_NAME, null, value);
            if (id > 0) {
                cnt++;
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        database.close();
        return cnt;
    }
}
