package me.loskutov.popularphotosviewer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by ignat on 18.01.15.
 */
public class DbSaveTask extends AsyncTask<Void, Void, Void> {
    private ArrayList<Photo> photos;
    private DBHelper helper;

    public DbSaveTask(DBHelper helper, ArrayList<Photo> photos) {
        this.helper = helper;
        this.photos = photos;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.clear(db);
        for(Photo photo : photos) {
            if(photo != null) {
                ContentValues cv = new ContentValues();
                cv.put("photoid", photo.id);
                cv.put("url", photo.url);
                cv.put("orig", photo.orig);
                cv.put("large", photo.large);
                cv.put("preview", photo.preview);
                db.insert("PHOTOS", null, cv);
            }
        }
        db.close();
        return null;
    }
}
