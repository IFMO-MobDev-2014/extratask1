package ru.ifmo.md.flickrclient;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by sultan on 15.01.15.
 */
public class FlickrContentProvider extends ContentProvider {
    private static String AUTHORITY = "ru.ifmo.md.flickrclient.flickrContentProvider";

    public static final Uri PHOTO_URI = Uri.parse("content://" + AUTHORITY + "/photo");

    private DBFlickr dbWeather;

    @Override
    public boolean onCreate() {
        dbWeather = new DBFlickr(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbWeather.getReadableDatabase();
        return db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbWeather.getWritableDatabase();
        String tableName = uri.getLastPathSegment();
        long id = db.insert(tableName, null, values);
        getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(PHOTO_URI, id), null);
        return Uri.parse("content://" + AUTHORITY + "/" + tableName + "/" + Long.toString(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbWeather.getWritableDatabase();
        int cnt =  db.delete(uri.getLastPathSegment(), selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbWeather.getWritableDatabase();
        int cnt = db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(PHOTO_URI, null);
        return cnt;
    }
}
