package ru.ifmo.md.extratask;

import android.content.ContentValues;

/**
 * Created by gshark on 16.03.15
 */
class Photo {
    private final String preview;
    private final String large;
    private final String id;
    private final String orig;
    private final String url;

    public Photo(String id, String preview, String large, String orig, String url) {
        this.id = id;
        this.preview = preview;
        this.large = large;
        this.orig = orig;
        this.url = url;
    }

    public ContentValues getCV() {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_ID, id);
        cv.put(DBHelper.COLUMN_URL, url);
        cv.put(DBHelper.COLUMN_ORIG, orig);
        cv.put(DBHelper.COLUMN_LARGE, large);
        cv.put(DBHelper.COLUMN_PREVIEW, preview);
        return cv;
    }

    public String getPreview() {
        return preview;
    }

    public String getLarge() {
        return large;
    }

    public String getId() {
        return id;
    }

    public String getOrig() {
        return orig;
    }

    public String getUrl() {
        return url;
    }
}
