package ru.ifmo.md.flickrclient;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

/**
 * Created by sultan on 16.01.15.
 */
public class FlickrCursorLoader extends CursorLoader {
    private long rowId;

    public FlickrCursorLoader(Context context, long rowId) {
        super(context);
        this.rowId = rowId;
    }

    @Override
    public Cursor loadInBackground() {
        return getContext().getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, DBFlickr.ID1 + " >= ?",
                new String[]{String.valueOf(rowId)}, MainActivity.sortOrder);
    }
}
