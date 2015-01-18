package ru.ya.popularfotki.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import ru.ya.popularfotki.R;

/**
 * Created by vanya on 17.01.15.
 */
public class FotkiContentProvider extends ContentProvider {
    //public static String AUTHORITY;
    public static final String AUTHORITY = "ru.ya.popularfotki.database.FotkiContentProvider";
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
        Log.e("in: ", " query");
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
        Log.e("insert:", "contentProvider");
        long id = fotkiSQLiteHelper.getWritableDatabase().insert(FotkiSQLiteHelper.PICTURES_TABLE, null, values);
        upd(uri);
        return Uri.parse(PICTURE_PATH + "/" + id);
    }

    private void upd(Uri uri) {
        if (flagUpdate) {
            Log.e("upd:", "CP");
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        flagUpdate = false;
        for (ContentValues value : values)
            insert(uri, value);
        flagUpdate = true;
        upd(uri);
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = fotkiSQLiteHelper.getWritableDatabase().delete(
                FotkiSQLiteHelper.PICTURES_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new Error();
    }
}
