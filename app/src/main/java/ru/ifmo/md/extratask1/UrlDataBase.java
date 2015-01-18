package ru.ifmo.md.extratask1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pinguinson on 16.01.2015.
 */
public class UrlDataBase extends SQLiteOpenHelper {
    public UrlDataBase(Context context) {
        super(context, "imagedb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table images ("
                + "_id integer primary key autoincrement,"
                + "url text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("drop table if exists images");
    }

    public List<Photo> getUrls() {
        List<Photo> photos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("images", null, null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String url = c.getString(1);
            photos.add(new Photo(url));
            c.moveToNext();
        }
        c.close();

        return photos;
    }

    public void setUrls(List<Photo> photos) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from images");

        for (Photo photo : photos) {
            ContentValues values = new ContentValues();
            values.put("url", photo.getFullURL());
            db.insert("images", null, values);
        }
    }
}
