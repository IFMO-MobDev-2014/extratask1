package com.example.heat_wave.photoviewer.database;

/**
 * Created by heat_wave on 18.01.15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.heat_wave.photoviewer.models.Photo;

import java.io.ByteArrayOutputStream;

public class PhotoDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "PhotoDB";

    // Books table name
    private static final String TABLE_PHOTOS = "photos";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_THUMBNAILURL = "thumbnailurl";
    private static final String KEY_FULLURL = "fullurl";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String KEY_FULL = "full";

    private static final String[] COLUMNS = {KEY_ID, KEY_THUMBNAILURL, KEY_FULLURL,
        KEY_THUMBNAIL, KEY_FULL};

    public PhotoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create table
        String CREATE_TABLE = "CREATE TABLE photos ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "thumbnailurl TEXT, " +
                "fullurl TEXT, " +
                "full BLOB, " +
                "thumbnail BLOB)";

        // create table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS photos");

        // create fresh table
        this.onCreate(db);
    }

    public void addPhoto(Photo photo){
        //for logging
        Log.d("addPhoto", photo.getFullURL());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_THUMBNAILURL, photo.getThumbnailURL());
        values.put(KEY_FULLURL, photo.getFullURL());
        values.put(KEY_THUMBNAIL, getBitmapAsByteArray(photo.getThumbnail()));
        values.put(KEY_FULL, getBitmapAsByteArray(photo.getFull()));


        // 3. insert
        db.insert(TABLE_PHOTOS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Bitmap getImageThumbnail(int i){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select full from photos where id=" + i ;
        Cursor cur = db.rawQuery(query, null);
        Bitmap bitmap = null;

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }
        return bitmap;
    }

    public Bitmap getImageFull(int i){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select thumbnail from photos where id=" + i ;
        Cursor cur = db.rawQuery(query, null);
        Bitmap bitmap = null;

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }
        return bitmap;
    }

    public int getPhotosCount() {
        String countQuery = "SELECT * FROM " + TABLE_PHOTOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public String getLink(int id) {
        String countQuery = "SELECT fullurl FROM photos where id=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()){
            String link = cursor.getString(0);
            cursor.close();
            return link;
        }
        return null;
    }

}