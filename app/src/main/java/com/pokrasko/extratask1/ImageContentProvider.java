package com.pokrasko.extratask1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ImageContentProvider extends ContentProvider {
    static String DB_NAME = "images.db";
    static int DB_VERSION = 1;

    private static final int IMAGES = 0;
    private static final int IMAGE_ID = 1;

    static final String ID_FIELD = "id";
    static final String INDEX_FIELD = "ind";
    static final String TITLE_FIELD = "title";
    static final String FULL_FIELD = "hLink";
    static final String PAGE_FIELD = "aLink";

    private static final String AUTHORITY = "com.pokrasko.extratask1";
    private static final String IMAGES_PATH = "images";
    public static final Uri CONTENT_IMAGES_URI = Uri.parse("content://" +
        AUTHORITY + "/" + IMAGES_PATH);

    private static final String IMAGES_TABLE = "images";

    private static final String CREATE_IMAGES_TABLE = "CREATE TABLE " + IMAGES_TABLE +
            " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", " + INDEX_FIELD + " INTEGER" +
            ", " + TITLE_FIELD + " TEXT NOT NULL UNIQUE" +
            ", " + FULL_FIELD + " TEXT NOT NULL UNIQUE" +
            ", " + PAGE_FIELD + " TEXT NOT NULL UNIQUE);";

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(AUTHORITY, IMAGES_PATH, IMAGES);
        matcher.addURI(AUTHORITY, IMAGES_PATH + "/#", IMAGE_ID);
    }

    private ImageDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ImageDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return Integer.toString(matcher.match(uri));
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        int uriType = matcher.match(uri);

        switch (uriType) {
            case IMAGES:
                id = db.insert(IMAGES_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, "" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = matcher.match(uri);

        int updatedRows;
        switch (uriType) {
            case IMAGE_ID:
                String imageId = uri.getLastPathSegment();
                updatedRows = db.update(IMAGES_TABLE, values, ID_FIELD + "=" + imageId, null);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = matcher.match(uri);

        switch (uriType) {
            case IMAGES:
                queryBuilder.setTables(IMAGES_TABLE);
                break;
            case IMAGE_ID:
                queryBuilder.setTables(IMAGES_TABLE);
                queryBuilder.appendWhere("id=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = matcher.match(uri);

        int deletedRows;
        switch (uriType) {
            case IMAGES:
                deletedRows = db.delete(IMAGES_TABLE, selection, selectionArgs);
                break;
            case IMAGE_ID:
                String imageId = uri.getLastPathSegment();
                deletedRows = db.delete(IMAGES_TABLE, ID_FIELD + "=" + imageId, null);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }


    public class ImageDBHelper extends SQLiteOpenHelper {
        public ImageDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(CREATE_IMAGES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int o, int n) {
            db.execSQL("DROP TABLE IF EXISTS images");
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int o, int n) {
            db.execSQL("DROP TABLE IF EXISTS images");
            onCreate(db);
        }
    }
}
