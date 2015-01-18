/*
 * This source file is generated with https://github.com/BoD/android-contentprovider-generator
 */
package ru.ifmo.zakharvoit.extratask1.provider.picture;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;

import ru.ifmo.zakharvoit.extratask1.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code picture} table.
 */
public class PictureContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return PictureColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, PictureSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public PictureContentValues putTitle(String value) {
        if (value == null) throw new IllegalArgumentException("value for title must not be null");
        mContentValues.put(PictureColumns.TITLE, value);
        return this;
    }



    public PictureContentValues putContents(byte[] value) {
        if (value == null) throw new IllegalArgumentException("value for contents must not be null");
        mContentValues.put(PictureColumns.CONTENTS, value);
        return this;
    }



    public PictureContentValues putLargeLink(String value) {
        if (value == null) throw new IllegalArgumentException("value for largeLink must not be null");
        mContentValues.put(PictureColumns.LARGE_LINK, value);
        return this;
    }


}
