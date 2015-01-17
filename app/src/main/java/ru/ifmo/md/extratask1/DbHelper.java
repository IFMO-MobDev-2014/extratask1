package ru.ifmo.md.extratask1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 17/01/15.
 */
public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
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

    public List<String> getUrls() {
        List<String> urls = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("images", null, null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String url = c.getString(1);
            urls.add(url);
            c.moveToNext();
        }
        c.close();

        return urls;
    }

    public void setUrls(List<String> urls) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from images");

        for (String url : urls) {
            ContentValues values = new ContentValues();
            values.put("url", url);
            db.insert("images", null, values);
        }
    }
}
