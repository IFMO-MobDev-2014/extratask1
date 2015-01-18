package ru.ifmo.md.extratask1.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Mikhail on 18.01.15.
 */
public class ImageContentProvider extends ContentProvider {
    private static String AUTHORITY = "ru.ifmo.md.extratask1.db.ImageContentProvider";

    public static final Uri LINK_CONTENT_URL = Uri.parse("content://" + AUTHORITY + "/links");

    private ImageDBHelper iDbHelper;

    private String getTableName(Uri uri) {
        return getTableName(uri.getLastPathSegment());
    }

    private String getTableName(String type) {
        String tableName;
        if(type.equals("links")) {
            tableName = iDbHelper.TABLE_LINK;
        } else {
            throw new UnsupportedOperationException("Invalid data type");
        }
        return tableName;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = iDbHelper.getWritableDatabase();
        return db.delete(getTableName(uri), selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = iDbHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        long id = db.insert(tableName, null, values);
        return Uri.parse("content://" + AUTHORITY + "/" + tableName + "/" + Long.toString(id));
    }

    @Override
    public boolean onCreate() {
        iDbHelper = new ImageDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = iDbHelper.getReadableDatabase();
        return db.query(getTableName(uri), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = iDbHelper.getWritableDatabase();
        return db.update(getTableName(uri), values, selection, selectionArgs);
    }
}
