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

    public static final String TITLE = "title";

    public static final String CONTENTS = "contents";

    public static final String LARGE_LINK = "large_link";

    public static final String MY_ID = "my_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            TITLE,
            CONTENTS,
            LARGE_LINK,
            MY_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c == TITLE || c.contains("." + TITLE)) return true;
            if (c == CONTENTS || c.contains("." + CONTENTS)) return true;
            if (c == LARGE_LINK || c.contains("." + LARGE_LINK)) return true;
            if (c == MY_ID || c.contains("." + MY_ID)) return true;
        }
        return false;
    }

}
