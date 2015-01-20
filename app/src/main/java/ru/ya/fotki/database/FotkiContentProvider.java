package ru.ya.fotki.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by vanya on 17.01.15.
 */
public class FotkiContentProvider extends ContentProvider {
    //public static String AUTHORITY;
    public static final String AUTHORITY = "ru.ya.fotki.database.FotkiContentProvider";
    public static final String PICTURE_PATH = "PICTURE_PATH";
    private static Boolean flagUpdate = true;
    private FotkiSQLiteHelper fotkiSQLiteHelper;

    public static final Uri FOTKI_URI = Uri.parse("content://" + AUTHORITY + "/" + PICTURE_PATH);

    @Override
    public boolean onCreate() {
//        Log.e("onCreate: ", "ContentProvider");
        fotkiSQLiteHelper = new FotkiSQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Log.e("in: ", " query");
        Cursor cursor = fotkiSQLiteHelper.getReadableDatabase().query(FotkiSQLiteHelper.PICTURES_TABLE,
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Log.e("insert:", "contentProvider");
        SQLiteDatabase db = fotkiSQLiteHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        db.insert(FotkiSQLiteHelper.PICTURES_TABLE, null, values);
        return null;
    }

//    private void upd(Uri uri) {
//        if (flagUpdate) {
//            Log.e("upd:", "CP");
//        }

//
//    @Override
//    public int bulkInsert(Uri uri, ContentValues[] values) {
//        flagUpdate = false;
//        for (ContentValues value : values)
//            insert(uri, value);
//        flagUpdate = true;
//        upd(uri);
//        return 0;
//    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = fotkiSQLiteHelper.getWritableDatabase();
        db.delete(FotkiSQLiteHelper.PICTURES_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = fotkiSQLiteHelper.getWritableDatabase();
        db.update(FotkiSQLiteHelper.PICTURES_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
}
