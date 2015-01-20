package ru.ifmo.md.photoclient;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Шолохов on 16.01.2015.
 */

public class MyContentProvider extends ContentProvider {
    public static final String DATABASE_NAME =  "weather.db";
    public static final String TABLE_PHOTOS = "tabLinks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PHOTO_NAME = "city_id";
    public static final String COLUMN_PHOTO_LINK_ONSITE = "name";
    public static final String COLUMN_PHOTO_LINK_LARGE = "weather";
    public static final String COLUMN_PHOTO_TAB = "weather_icon";


    public static final String AUTHORITY = "ru.ifmo.md.photoclient";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CITIES_CREATE = "create table "
            + TABLE_PHOTOS + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PHOTO_NAME + " text, "
            + COLUMN_PHOTO_LINK_ONSITE + " text, "
            + COLUMN_PHOTO_LINK_LARGE + " text, "
            + COLUMN_PHOTO_TAB + " text "
            +");";


    public static final Uri TABLE_PHOTOS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_PHOTOS);

    static final int PHOTOS = 1;
    static final int PHOTOS_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, TABLE_PHOTOS, PHOTOS);
        uriMatcher.addURI(AUTHORITY, TABLE_PHOTOS + "/#", PHOTOS_ID);
    }

    MySQLiteHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new MySQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        SQLiteQueryBuilder sqB = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case PHOTOS:
                sqB.setTables(TABLE_PHOTOS);
                break;
            case PHOTOS_ID:
                sqB.setTables(TABLE_PHOTOS);
                sqB.appendWhere(COLUMN_PHOTO_TAB+"="+uri.getLastPathSegment());
                break;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = sqB.query(db, strings, s, strings2, null, null, s2);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase sqlQB = dbHelper.getWritableDatabase();
        long insertedID=1;
        switch (uriMatcher.match(uri)) {
            case PHOTOS:
                insertedID = sqlQB.insert(TABLE_PHOTOS, null, contentValues);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, Long.toString(insertedID));
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase sqlQB = dbHelper.getWritableDatabase();
        int result = 0;
        String ending;
        switch (uriMatcher.match(uri)) {
            case PHOTOS:
                ending = uri.getLastPathSegment();
                if (TextUtils.isEmpty(s)) {
                    result = sqlQB.delete(TABLE_PHOTOS, null, null);
                }
                else {
                    result = sqlQB.delete(TABLE_PHOTOS,  s, strings);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        SQLiteDatabase sqlQB = dbHelper.getWritableDatabase();
        int result = 0;
        String ending;
        switch (uriMatcher.match(uri)) {
            case PHOTOS_ID:
                ending = uri.getLastPathSegment();
                if (TextUtils.isEmpty(s)) {
                    result = sqlQB.update(TABLE_PHOTOS, contentValues, COLUMN_PHOTO_NAME + " = "+ ending, null);
                }
                else {
                    result = sqlQB.update(TABLE_PHOTOS, contentValues, COLUMN_PHOTO_NAME + " = "+ ending + " and " + s, strings);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    public class MySQLiteHelper extends SQLiteOpenHelper {


        public MySQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CITIES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(MySQLiteHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
            onCreate(db);
        }
    }

}
