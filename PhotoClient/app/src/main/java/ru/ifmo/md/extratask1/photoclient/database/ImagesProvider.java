package ru.ifmo.md.extratask1.photoclient.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by sergey on 09.11.14.
 */
public class ImagesProvider extends ContentProvider {

    private ImagesDatabase imagesDatabase;

    private static final int IMAGES = 10;
    private static final int IMAGES_ID = 20;

    private static final String CONTENT_AUTHORITY = "ru.ifmo.md.extratask1.photoclient";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_IMAGES = "images";

    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + PATH_IMAGES);

    public static Uri buildImageUri(String imageId) {
        return CONTENT_URI.buildUpon().appendPath(imageId).build();
    }

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_IMAGES, IMAGES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_IMAGES + "/#", IMAGES_ID);
    }

    @Override
    public boolean onCreate() {
        imagesDatabase = new ImagesDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = uriMatcher.match(uri);
        queryBuilder.setTables(ImagesTable.TABLE_NAME);
        switch (uriType) {
            case IMAGES:
                break;
            case IMAGES_ID:
                queryBuilder.appendWhere(ImagesTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        SQLiteDatabase db = imagesDatabase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = imagesDatabase.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case IMAGES:
                id = db.insert(ImagesTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = imagesDatabase.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case IMAGES:
                rowsDeleted = db.delete(ImagesTable.TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ImagesTable.TABLE_NAME, ImagesTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(ImagesTable.TABLE_NAME, ImagesTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = imagesDatabase.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case IMAGES:
                rowsUpdated = db.update(ImagesTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case IMAGES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ImagesTable.TABLE_NAME, values, ImagesTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(ImagesTable.TABLE_NAME, values, ImagesTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}