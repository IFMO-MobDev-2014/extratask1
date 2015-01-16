package com.photofinder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    private static String AUTHORITY = "com.photofinder.MyContentProvider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/" + DataBaseHelper.TABLE_PICS);

    private DataBaseHelper dataBaseHelper;

    @Override
    public boolean onCreate() {
        dataBaseHelper = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        return sqLiteDatabase.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        String name = uri.getLastPathSegment();
        long id = sqLiteDatabase.insert(name, null, values);
        return Uri.parse("content://" + AUTHORITY + "/" + name + "/" + Long.toString(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return sqLiteDatabase.delete(uri.getLastPathSegment(), selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return sqLiteDatabase.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }

}