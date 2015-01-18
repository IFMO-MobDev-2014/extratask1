package ru.ifmo.md.extratask1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "imagedb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table images ("
                + "_id integer primary key autoincrement,"
                + "url text,"
                + "title text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("drop table if exists images");
    }

    public List<Image> getImages() {
        List<Image> images = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("images", null, null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String url = c.getString(1);
            String image = c.getString(2);
            images.add(new Image(url, image));
            c.moveToNext();
        }
        c.close();

        return images;
    }

    public void setImages(List<Image> images) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from images");

        for (Image image : images) {
            ContentValues values = new ContentValues();
            values.put("url", image.url);
            values.put("title", image.title);
            db.insert("images", null, values);
        }
    }
}
