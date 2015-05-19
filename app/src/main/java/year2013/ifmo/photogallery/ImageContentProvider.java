package year2013.ifmo.photogallery;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class ImageContentProvider extends ContentProvider {

    public static final String JUST_IMAGE = "just_image";
    public static final String IMAGE_TABLE_NAME = "image";

    private static final int IMAGE = 1;
    private static final int IMAGE_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Gallery.AUTHORITY, Gallery.Images.PATH, IMAGE);
        uriMatcher.addURI(Gallery.AUTHORITY, Gallery.Images.PATH + "/#", IMAGE_ID);
    }

    private static class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = JUST_IMAGE + ".db";
        private static int DATABASE_VERSION = 6;

        private DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTables(sqLiteDatabase);
        }

        private void createTables(SQLiteDatabase sqLiteDatabase) {
            String qs = "CREATE TABLE " + IMAGE_TABLE_NAME + " ("
                    + Gallery.Images._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Gallery.Images.SMALL_IMAGE + " BLOB, "
                    + Gallery.Images.LARGE_IMAGE_URL + " TEXT, "
                    + Gallery.Images.ORIG_IMAGE_URL + " TEXT, "
                    + Gallery.Images.LARGE_PATH_NAME + " TEXT, "
                    + Gallery.Images.TITLE + " TEXT, "
                    + Gallery.Images.LAST_UPDATE + " TEXT " + ");";
            sqLiteDatabase.execSQL(qs);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE_NAME + ";");
            createTables(sqLiteDatabase);
        }
    }

    public ImageContentProvider() {
    }

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case IMAGE:
                return Gallery.Images.CONTENT_TYPE;
            case IMAGE_ID:
                return Gallery.Images.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown type: " + uri);
        }
    }

    private SQLiteDatabase getDb() {
        return dbHelper.getWritableDatabase();
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        int u = uriMatcher.match(uri);
        if (u != IMAGE) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = initialValues;
        } else {
            values = new ContentValues();
        }

        db = getDb();

        long rowID = db.insert(IMAGE_TABLE_NAME, null, values);
        if (rowID > 0) {
            Uri resultUri = ContentUris.withAppendedId(Gallery.Images.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affected;

        db = getDb();

        switch (uriMatcher.match(uri)) {
            case IMAGE:
                affected = db.delete(IMAGE_TABLE_NAME,
                        (!TextUtils.isEmpty(selection) ?
                                " (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case IMAGE_ID:
                long id = ContentUris.parseId(uri);
                affected = db.delete(IMAGE_TABLE_NAME,
                        Gallery.Images._ID + "=" + id
                                + (!TextUtils.isEmpty(selection) ?
                                " (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown feed element: " +
                        uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        if (match == IMAGE_ID) {
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                selection = Gallery.Images._ID + " = " + id;
            } else {
                selection = selection + " AND " + Gallery.Images._ID + " = " + id;
            }
        } else if (match != IMAGE) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = getDb();
        Cursor cursor = db.query(IMAGE_TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                Gallery.Images.CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int affected;

        db = getDb();
        switch (uriMatcher.match(uri)) {
            case IMAGE:
                affected = db.update(IMAGE_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case IMAGE_ID:
                String id = uri.getPathSegments().get(1);
                affected = db.update(IMAGE_TABLE_NAME, values,
                        Gallery.Images._ID + "=" + id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return affected;
    }
}