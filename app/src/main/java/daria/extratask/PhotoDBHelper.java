package daria.extratask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by daria on 18.01.15.
 */
public class PhotoDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "images.db";

    private static final String TABLE_IMAGES = "images";

    private static final String KEY_ID = "id";
    private static final String KEY_THUMBNAIL_URL = "thumbnail_url";
    private static final String KEY_FULL_URL = "full_url";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String KEY_FULL = "full";

    public PhotoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_IMAGES = "CREATE TABLE images ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "thumbnail_url TEXT, "
                + "full_url TEXT, "
                + "thumbnail BLOB, "
                + "full BLOB)";

        db.execSQL(CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS images");

        this.onCreate(db);
    }

    public void addPhoto(Photo photo) {
        Log.d("ADD_PHOTO", photo.getFullImageURL());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_THUMBNAIL_URL, photo.getThumbnailURL());
        values.put(KEY_FULL_URL, photo.getFullImageURL());
        values.put(KEY_THUMBNAIL, getByteArray(photo.getThumbnail()));
        values.put(KEY_FULL, getByteArray(photo.getFullImage()));

        db.insert(TABLE_IMAGES, null, values);

        db.close();
    }

    public static byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Bitmap getThumbnail(int i) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select thumbnail from images where id=" + i;
        Cursor cursor = db.rawQuery(query, null);
        Bitmap bitmap = null;

        if (cursor.moveToFirst()) {
            byte[] imageByte = cursor.getBlob(0);
            cursor.close();
            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return bitmap;
    }

    public int getPhotosCount() {
        String countQuery = "SELECT * FROM " + TABLE_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public String getLink(int id) {
        String linkQuery = "SELECT full_url FROM images where id=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(linkQuery, null);
        if (cursor.moveToFirst()) {
            String link = cursor.getString(0);
            cursor.close();
            return link;
        }
        return null;
    }

    public Bitmap getImageFull(int i){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select full from images where id=" + i ;
        Cursor cur = db.rawQuery(query, null);
        Bitmap bitmap = null;

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (!cur.isClosed()) {
            cur.close();
        }
        return bitmap;
    }
}


