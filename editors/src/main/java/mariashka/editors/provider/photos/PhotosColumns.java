package mariashka.editors.provider.photos;

import android.net.Uri;
import android.provider.BaseColumns;

import mariashka.editors.provider.Provider;
import mariashka.editors.provider.photos.PhotosColumns;

/**
 * Columns for the {@code photos} table.
 */
public class PhotosColumns implements BaseColumns {
    public static final String TABLE_NAME = "photos";
    public static final Uri CONTENT_URI = Uri.parse(Provider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = new String(BaseColumns._ID);

    public static final String NAME = "name";

    public static final String SMALL_IMG = "small_img";

    public static final String BIG_IMG = "big_img";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NAME,
            SMALL_IMG,
            BIG_IMG
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c == NAME || c.contains("." + NAME)) return true;
            if (c == SMALL_IMG || c.contains("." + SMALL_IMG)) return true;
            if (c == BIG_IMG || c.contains("." + BIG_IMG)) return true;
        }
        return false;
    }

}
