package com.example.maksim.photoview;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import android.database.SQLException;
import android.util.Log;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.maksim.photoview";
    private static final String IMAGES_PATH = SQLiteHelper.IMAGE_TABLE;
    public static final Uri IMAGES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + IMAGES_PATH);

    private SQLiteHelper helper;

    public boolean onCreate() {
        helper = new SQLiteHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues cv) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String name = uri.getLastPathSegment();
        long id = db.insert(name, null, cv);
        Uri res = Uri.parse("content://" + AUTHORITY + "/" + name + "/" + Long.toString(id));
        return res;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int cnt = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int cnt = db.update(uri.getLastPathSegment(), cv, selection, selectionArgs);
        return cnt;
    }
}