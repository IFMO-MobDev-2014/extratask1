/*
 * This source file is generated with https://github.com/BoD/android-contentprovider-generator
 */
package ru.ifmo.zakharvoit.extratask1.provider.picture;

import java.util.Date;

import android.database.Cursor;

import ru.ifmo.zakharvoit.extratask1.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code picture} table.
 */
public class PictureCursor extends AbstractCursor {
    public PictureCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code title} value.
     * Cannot be {@code null}.
     */
    public String getTitle() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.TITLE);
        return getString(index);
    }

    /**
     * Get the {@code contents} value.
     * Cannot be {@code null}.
     */
    public byte[] getContents() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.CONTENTS);
        return getBlob(index);
    }

    /**
     * Get the {@code large_link} value.
     * Cannot be {@code null}.
     */
    public String getLargeLink() {
        Integer index = getCachedColumnIndexOrThrow(PictureColumns.LARGE_LINK);
        return getString(index);
    }
}
