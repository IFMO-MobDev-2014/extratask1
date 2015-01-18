package com.example.vlad107.extratask1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class DataBaseProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.vlad107.images";
    public static final Uri IMAGE_URI = Uri.parse("content://" + AUTHORITY + "/" + ImagesContract.TABLE_NAME);

    DataBaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return dbHelper.getReadableDatabase().query(ImagesContract.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowId;
        rowId = dbHelper.getWritableDatabase().insert(
                ImagesContract.TABLE_NAME,
                null,
                contentValues);
        return ContentUris.withAppendedId(IMAGE_URI, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return dbHelper.getWritableDatabase().delete(
                ImagesContract.TABLE_NAME,
                selection,
                selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs) {
        return dbHelper.getWritableDatabase().update(
                ImagesContract.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);
    }
}
