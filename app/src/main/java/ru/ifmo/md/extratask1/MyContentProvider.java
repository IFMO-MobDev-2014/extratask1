package ru.ifmo.md.extratask1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Svet on 16.01.2015.
 */
public class MyContentProvider extends ContentProvider implements BaseColumns {

    //constants
    static final String DB_NAME = "mydatabase";
    static final int DB_VERSION = 1;

    //tables
    static final String IMAGE_TABLE = "image_table";

    //colums
    static final String TITLE = "title";
    static final String URLL = "urlL";
    static final String URLXXXL = "urlXXXL";
    static final String AUTHOR = "author";
    static final String ADDRESSL = "addressL";
    static final String ADDRESSXXXL = "addressXXXL";

    //create table script
    static final String DB_CREATE = "create table " + IMAGE_TABLE + "(" +
            _ID + " integer primary key autoincrement, " +
            TITLE + " text, " +
            URLL + " text, " +
            URLXXXL + " text, " +
            AUTHOR + " text, " +
            ADDRESSL + " text, " +
            ADDRESSXXXL + " text " +
            ");";

    //Uri
    //autority
    static final String AUTHORITY = "ru.ifmo.md.ImageData";

    //path
    static final String IMAGE_PATH = "images";

    //overall uri
    public static final Uri IMAGE_CONTENT_URI = Uri.parse("content://" +
     AUTHORITY + "/" + IMAGE_PATH);

    //types
    //set of items
    static final String IMAGE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." +
            AUTHORITY + "." + IMAGE_PATH;

    //single item
    static final String IMAGE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." +
            AUTHORITY + "." + IMAGE_PATH;

    //UriMatcher
    // overall Uri
    static final int URI_IMAGES = 1;

    // Uri with fixed ID
    static final int URI_IMAGES_ID = 2;

    //UriMatcher description and creation
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, IMAGE_PATH, URI_IMAGES);
        uriMatcher.addURI(AUTHORITY, IMAGE_PATH + "/#", URI_IMAGES_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase sql;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //check Urk
        selection = makeRoutine(uri, selection);

        sql = dbHelper.getWritableDatabase();
        Cursor cursor = sql.query(IMAGE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

        //set notification flag on cursor
        cursor.setNotificationUri(getContext().getContentResolver(), IMAGE_CONTENT_URI);

        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if(uriMatcher.match(uri) != URI_IMAGES)
            throw new IllegalArgumentException("Wrong URI: " + uri.toString());

        sql = dbHelper.getWritableDatabase();

        long rowID = sql.insert(IMAGE_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(IMAGE_CONTENT_URI, rowID);
        //notify ContentResolver about changes
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_IMAGES :
                return IMAGE_CONTENT_TYPE;
            case URI_IMAGES_ID :
                return IMAGE_CONTENT_ITEM_TYPE;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        selection = makeRoutine(uri, selection);

        sql = dbHelper.getWritableDatabase();
        int count = sql.delete(IMAGE_TABLE, selection, selectionArgs);
        //notify
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        selection = makeRoutine(uri, selection);

        sql = dbHelper.getWritableDatabase();
        int count = sql.update(IMAGE_TABLE, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private String makeRoutine(Uri uri, String selection) {
        switch (uriMatcher.match(uri)) {
            case URI_IMAGES : {
                break;
            }
            case URI_IMAGES_ID: {
                String id = uri.getLastPathSegment();
                if(selection.isEmpty()) {
                    selection = _ID + " = " + id;
                } else {
                    selection = selection + " AND " + _ID + " = " + id;
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri.toString());
        }
        return selection;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}