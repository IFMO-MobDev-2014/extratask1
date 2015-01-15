/*
 * This source file is generated with https://github.com/BoD/android-contentprovider-generator
 */
package ru.ifmo.zakharvoit.extratask1.provider.picture;

import android.net.Uri;
import android.provider.BaseColumns;

import ru.ifmo.zakharvoit.extratask1.provider.Provider;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureColumns;

/**
 * Columns for the {@code picture} table.
 */
public class PictureColumns implements BaseColumns {
    public static final String TABLE_NAME = "picture";
    public static final Uri CONTENT_URI = Uri.parse(Provider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = new String(BaseColumns._ID);

    public static final String NAME = "name";

    public static final String CONTENTS = "contents";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NAME,
            CONTENTS
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c == NAME || c.contains("." + NAME)) return true;
            if (c == CONTENTS || c.contains("." + CONTENTS)) return true;
        }
        return false;
    }

}
