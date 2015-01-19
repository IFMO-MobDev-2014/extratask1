package com.adelnator.extratask1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adel on 18.01.15.
 */

public class DataBase  {

    private static final String DB_NAME = "photos";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "yandex_photos";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WIDTH = "width";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_BIG_IMAGE = "big";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_WIDTH + " integer, " +
                    COLUMN_HEIGHT + " integer," +
                    COLUMN_BIG_IMAGE + " blob" +
                    ");";

    private final Context context;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DataBase(Context context) {
        this.context = context;
    }

    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }


    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public void addChannel(int width, int height, Bitmap bitmap) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WIDTH, width);
        cv.put(COLUMN_HEIGHT, height);
        cv.put(COLUMN_BIG_IMAGE, decodeBitmap(bitmap));
        mDB.insert(DB_TABLE, null, cv);
    }

    private byte[] decodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public List<Bitmap> getAllPicturesData() {
        List<Bitmap> list = new ArrayList<Bitmap>();
        Cursor cursor = getAllData();
        while (cursor.moveToNext()) {
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_BIG_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            list.add(bitmap);
        }
        return list;
    }

    public void deleteAllChannels() {
        mDB.delete(DB_TABLE, null, null);
    }

    public Bitmap getPicture(int index) {
        ArrayList<Bitmap> arrayList = (ArrayList<Bitmap>) getAllPicturesData();
        return arrayList.get(index);
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String dbName, Object o, int dbVersion) {
            super(context, dbName, (SQLiteDatabase.CursorFactory) o, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DataBase.DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }
}
