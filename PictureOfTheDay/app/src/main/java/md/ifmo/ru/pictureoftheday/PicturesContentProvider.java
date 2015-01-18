package md.ifmo.ru.pictureoftheday;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Илья on 17.01.2015.
 */
public class PicturesContentProvider extends ContentProvider {
    private static String AUTHORITY = "md.ifmo.ru.pictureoftheday.picturesContentProvider";

    public static final Uri PICTURES_URI = Uri.parse("content://" + AUTHORITY + "/" + DBPictures.TABLE_PICTURES);

    private DBPictures dbPictures;

    @Override
    public boolean onCreate() {
        dbPictures = new DBPictures(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbPictures.getReadableDatabase();
        return db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbPictures.getWritableDatabase();
        String tableName = uri.getLastPathSegment();
        long id = db.insert(tableName, null, values);
        return Uri.parse("content://" + AUTHORITY + "/" + tableName + "/" + Long.toString(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbPictures.getWritableDatabase();
        return db.delete(uri.getLastPathSegment(), selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbPictures.getWritableDatabase();
        return db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }

}
