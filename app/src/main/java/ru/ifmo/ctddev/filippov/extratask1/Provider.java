package ru.ifmo.ctddev.filippov.extratask1;

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
 * Created by Dima_2 on 01.03.2015.
 */
public class Provider extends ContentProvider {
    static final String PHOTO_ID = "_id";
    public static final String AUTHORITY = "ru.ifmo.ctddev.filippov.extratask1";
    public static final Uri PHOTOS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MyContentProvider.PHOTOS_TABLE);
    static final int URI_PHOTOS = 1;
    static final int URI_PHOTOS_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MyContentProvider.PHOTOS_TABLE, URI_PHOTOS);
        uriMatcher.addURI(AUTHORITY, MyContentProvider.PHOTOS_TABLE + "/#", URI_PHOTOS_ID);
    }
    private SQLiteDatabase database;
    private MyContentProvider contentProvider;

    @Override
    public boolean onCreate() {
        contentProvider = new MyContentProvider(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case URI_PHOTOS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = PHOTO_ID + " ASC";
                }
                builder.setTables(MyContentProvider.PHOTOS_TABLE);
                break;
            case URI_PHOTOS_ID:
                String id = uri.getLastPathSegment();
                builder.setTables(MyContentProvider.PHOTOS_TABLE);
                if (TextUtils.isEmpty(selection)) {
                    selection = PHOTO_ID + " = " + id;
                } else {
                    selection = selection + " AND " + PHOTO_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = contentProvider.getWritableDatabase();
        Cursor cursor = builder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case URI_PHOTOS:
                table = MyContentProvider.PHOTOS_TABLE;
                resultUri = PHOTOS_CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = contentProvider.getWritableDatabase();
        long rowID = database.insert(table, null, values);
        resultUri = ContentUris.withAppendedId(resultUri, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        String id;
        switch (uriMatcher.match(uri)) {
            case URI_PHOTOS:
                table = MyContentProvider.PHOTOS_TABLE;
                break;
            case URI_PHOTOS_ID:
                id = uri.getLastPathSegment();
                table = MyContentProvider.PHOTOS_TABLE;
                if (TextUtils.isEmpty(selection)) {
                    selection = PHOTO_ID + " = " + id;
                } else {
                    selection = selection + " AND " + PHOTO_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = contentProvider.getWritableDatabase();
        int count = database.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;
        String id;
        switch (uriMatcher.match(uri)) {
            case URI_PHOTOS_ID:
                id = uri.getLastPathSegment();
                table = MyContentProvider.PHOTOS_TABLE;
                if (TextUtils.isEmpty(selection)) {
                    selection = PHOTO_ID + " = " + id;
                } else {
                    selection = selection + " AND " + PHOTO_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = contentProvider.getWritableDatabase();
        int count = database.update(table, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
