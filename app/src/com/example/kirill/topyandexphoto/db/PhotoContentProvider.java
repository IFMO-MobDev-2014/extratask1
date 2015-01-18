package com.example.kirill.topyandexphoto.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.kirill.topyandexphoto.db.model.ImageDataTable;


/**
 * Created by Kirill on 01.12.2014.
 */
public class PhotoContentProvider extends ContentProvider {

    private static final String IMAGES_TABLE = ImageDataTable.TABLE_NAME;

    private DBHelper helper;

    private static final int SINGLE_IMAGE = 1;
    private static final int ALL_IMAGES = 4;
    
    private static final String AUTHORITY = "com.example.kirill.topyandexphoto";
    private static final String PATH_IMAGES = "Images";

    public static final Uri CONTENT_URI_IMAGES =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_IMAGES);

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, PATH_IMAGES, ALL_IMAGES);
        uriMatcher.addURI(AUTHORITY, PATH_IMAGES + "/#", SINGLE_IMAGE);
    }
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case ALL_IMAGES:
                queryBuilder.setTables(IMAGES_TABLE);
                break;
            case SINGLE_IMAGE:
                queryBuilder.setTables(IMAGES_TABLE);
                queryBuilder.appendWhere(ImageDataTable._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case ALL_IMAGES:
                id = db.insert(ImageDataTable.TABLE_NAME, null, contentValues);
                break;
            case SINGLE_IMAGE:
                contentValues.put(ImageDataTable._ID, uri.getLastPathSegment());
                id = db.insert(ImageDataTable.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int removed;
        String id = "";
        switch (uriMatcher.match(uri)) {
            case ALL_IMAGES:
                removed = db.delete(ImageDataTable.TABLE_NAME, selection, selectionArgs);
                break;
            case SINGLE_IMAGE:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    removed = db.delete(ImageDataTable.TABLE_NAME, ImageDataTable._ID + "=" + id, selectionArgs);
                } else {
                    removed = db.delete(ImageDataTable.TABLE_NAME, ImageDataTable._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return removed;
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int updated;
        String id = "";
        switch (uriMatcher.match(uri)) {
            case ALL_IMAGES:
                updated = db.update(ImageDataTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case SINGLE_IMAGE:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    updated = db.update(ImageDataTable.TABLE_NAME, contentValues, ImageDataTable._ID + "=" + id, selectionArgs);
                } else {
                    updated = db.update(ImageDataTable.TABLE_NAME, contentValues, ImageDataTable._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updated;
    }

}