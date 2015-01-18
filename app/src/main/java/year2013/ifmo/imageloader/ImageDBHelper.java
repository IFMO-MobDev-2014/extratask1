package year2013.ifmo.imageloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Юлия on 18.01.2015.
 */

public class ImageDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ImageDB.db";
    public static final String SQL_CREATE_TABLE_IMAGES = "create table Images(Id integer primary key autoincrement, ImageSmall blob, SmallLink text, BigLink text)";
    public static final String SQL_DROP_TABLE_IMAGES = "drop table if exists Images";

    public ImageDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_IMAGES);
        onCreate(db);
    }

    public void SaveImages(SQLiteDatabase db, List<CustomImage> list){
        int cnt = list.size();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < cnt; i++) {
            cv.put("ImageSmall", ImageService.TranslateBitmapToByteArray(list.get(i).Bitmap));
            cv.put("SmallLink", list.get(i).SmallImageLink);
            cv.put("BigLink", list.get(i).BigImageLink);
            db.insert("Images", null, cv);
        }
    }

    public ArrayList<CustomImage> GetImages(SQLiteDatabase db){
        ArrayList<CustomImage> list = new ArrayList<CustomImage>();

        Cursor cursor = db.rawQuery("select * from Images",null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("Id"));
            byte[] imageByteArray=cursor.getBlob(cursor.getColumnIndex("ImageSmall"));

            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            String smallLink = cursor.getString(cursor.getColumnIndex("SmallLink"));
            String bigLink = cursor.getString(cursor.getColumnIndex("BigLink"));

            list.add(new CustomImage(smallLink, bigLink, bmp));
        }
        cursor.close();

        return list;
    }

    public void ClearImagesTable(SQLiteDatabase db){
        db.delete("Images", null, null);
    }

    private void startManagingCursor(Cursor cursor) {
    }
}
