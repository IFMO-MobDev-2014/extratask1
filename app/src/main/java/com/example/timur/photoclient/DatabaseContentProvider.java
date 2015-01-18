package com.example.timur.photoclient;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by timur on 18.01.15.
 */
public class DatabaseContentProvider extends ContentProvider {
    private static final String PHOTO_ID = "_id";
    private static final String AUTHORITY = "com.example.timur.photoclient";
    private static final String BAD_URI = "Invalid Uri";
    public static final Uri PHOTOS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PhotoTable.PHOTOS_TABLE);
    static final int URI_PHOTOS = 1;
    static final int URI_PHOTOS_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PhotoTable.PHOTOS_TABLE, URI_PHOTOS);
        uriMatcher.addURI(AUTHORITY, PhotoTable.PHOTOS_TABLE + "/#", URI_PHOTOS_ID);
    }

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper sqLiteDatabaseHelper;

    @Override
    public boolean onCreate() {
        sqLiteDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        int uriType = uriMatcher.match(uri);
        if (uriType == URI_PHOTOS) {
            if (TextUtils.isEmpty(sortOrder)) {
                sortOrder = "_id " + " ASC";
            }
            builder.setTables(PhotoTable.PHOTOS_TABLE);
            sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
            Cursor cursor = builder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } else if (uriType == URI_PHOTOS_ID) {
            String id = uri.getLastPathSegment();
            builder.setTables(PhotoTable.PHOTOS_TABLE);
            if (TextUtils.isEmpty(selection)) {
                selection = PHOTO_ID + " = " + id;
            } else {
                selection = selection + " AND " + PHOTO_ID + " = " + id;
            }
            sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
            Cursor cursor = builder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException(BAD_URI);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        if (uriType == URI_PHOTOS) {
            Uri resultUri;
            resultUri = PHOTOS_CONTENT_URI;
            sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
            long rowID = sqLiteDatabase.insert(PhotoTable.PHOTOS_TABLE, null, values);
            resultUri = ContentUris.withAppendedId(resultUri, rowID);
            getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        } else {
            throw new IllegalArgumentException(BAD_URI);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        if (uriType == URI_PHOTOS) {
            sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
            int count = sqLiteDatabase.delete(PhotoTable.PHOTOS_TABLE, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else if (uriType == URI_PHOTOS_ID) {
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                selection = PHOTO_ID + " = " + id;
            } else {
                selection = selection + " AND " + PHOTO_ID + " = " + id;
            }
            sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
            int count = sqLiteDatabase.delete(PhotoTable.PHOTOS_TABLE, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            throw new IllegalArgumentException(BAD_URI);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        if (uriType == URI_PHOTOS_ID) {
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                selection = PHOTO_ID + " = " + id;
            } else {
                selection = selection + " AND " + PHOTO_ID + " = " + id;
            }
            sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
            int count = sqLiteDatabase.update(PhotoTable.PHOTOS_TABLE, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
                throw new IllegalArgumentException(BAD_URI);
        }

    }
}