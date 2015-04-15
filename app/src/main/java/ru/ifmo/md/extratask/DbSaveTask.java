package ru.ifmo.md.extratask;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by gshark on 17.03.15
 */
public class DbSaveTask extends AsyncTask<Void, Void, Void> {
    private final ArrayList<Photo> photos;
    private final DBHelper helper;

    public DbSaveTask(DBHelper helper, ArrayList<Photo> photos) {
        this.helper = helper;
        this.photos = photos;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.clear(db);
        for (Photo photo : photos) {
            if (photo != null) {
                db.insert(DBHelper.PHOTOS_TABLE_NAME, null, photo.getCV());
            }
        }
        db.close();
        return null;
    }
}
