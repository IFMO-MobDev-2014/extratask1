package com.photofinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBaseAdapter {

    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;


    public DataBaseAdapter(Context context) {
        this.context = context;
    }

    public DataBaseAdapter open() {
        dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
        dbHelper.close();
    }

    public ArrayList<Bitmap> getAllPics() {
        ArrayList<Bitmap> pics = new ArrayList<Bitmap>();
        Cursor cursor = db.query(DataBaseHelper.TABLE_PICS, new String[] {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_PIC, DataBaseHelper.KEY_LINK, DataBaseHelper.KEY_XXL_LINK},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                byte[] rawData = cursor.getBlob((cursor.getColumnIndex(DataBaseHelper.KEY_PIC)));
                pics.add(BitmapFactory.decodeByteArray(rawData, 0, rawData.length));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pics;
    }

    public ArrayList<Pair<String, String>> getAllLinks() {
        ArrayList<Pair<String, String>> pics = new ArrayList<>();
        Cursor cursor = db.query(DataBaseHelper.TABLE_PICS, new String[] {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_PIC, DataBaseHelper.KEY_LINK, DataBaseHelper.KEY_XXL_LINK},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String link = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_LINK));
                String xxlLink = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_XXL_LINK));
                pics.add(new Pair<>(link, xxlLink));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pics;
    }

    public Cursor getContact(long rowId) throws SQLException {
        Cursor mCursor = db.query(true, DataBaseHelper.TABLE_PICS, new String[] {
                        DataBaseHelper.KEY_ID, DataBaseHelper.KEY_PIC, DataBaseHelper.KEY_LINK, DataBaseHelper.KEY_XXL_LINK }, DataBaseHelper.KEY_ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void changePicById(int id, Bitmap bitmap, String link, String xxlLink) {
            ContentValues values = new ContentValues();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] rawData = stream.toByteArray();
            values.put(DataBaseHelper.KEY_PIC, rawData);
            values.put(DataBaseHelper.KEY_LINK, link);
            values.put(DataBaseHelper.KEY_XXL_LINK, xxlLink);
        String where = "_id=?";
        String[] whereArgs = new String[] {String.valueOf(id)};
            db.update(DataBaseHelper.TABLE_PICS, values, where, whereArgs);
    }

    public Bitmap getPicById(int id) {
        Bitmap bitmap = null;
        Cursor cursor = db.query(DataBaseHelper.TABLE_PICS, new String[]{DataBaseHelper.KEY_ID, DataBaseHelper.KEY_PIC, DataBaseHelper.KEY_LINK, DataBaseHelper.KEY_XXL_LINK}, DataBaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        byte[] rawData = new byte[0];
        if (cursor != null) {
            rawData = cursor.getBlob((cursor.getColumnIndex(DataBaseHelper.KEY_PIC)));
        }
        bitmap = BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
        return bitmap;
    }

    public void addPic(Bitmap bitmap, Pair<String, String> link) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] rawData = stream.toByteArray();
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_PIC, rawData);
        values.put(DataBaseHelper.KEY_LINK, link.first);
        values.put(DataBaseHelper.KEY_XXL_LINK, link.second);
        db.insert(DataBaseHelper.TABLE_PICS, null, values);

    }



    public void deletePics() {
        db.delete(DataBaseHelper.TABLE_PICS, null, null);
    }

}
