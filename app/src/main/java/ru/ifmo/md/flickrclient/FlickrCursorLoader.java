package ru.ifmo.md.flickrclient;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

/**
 * Created by sultan on 16.01.15.
 */
public class FlickrCursorLoader extends CursorLoader {
    public FlickrCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return getContext().getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, null, null,
                MainActivity.sortOrder);
    }
}
