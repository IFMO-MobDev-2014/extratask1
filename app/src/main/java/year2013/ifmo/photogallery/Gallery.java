package year2013.ifmo.photogallery;


import android.net.Uri;
import android.provider.BaseColumns;

public class Gallery {
    public static final String AUTHORITY =
            "year2013.ifmo.photogallery.provider.image";

    public static final class Images implements BaseColumns {

        private Images() {}

        public static final String PATH = "image";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + Images.PATH);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.image.data";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.image.data";

        public static final String SMALL_IMAGE = "small";

        public static final String LARGE_IMAGE_URL = "large_url";

        public static final String ORIG_IMAGE_URL = "orig";

        public static final String LARGE_PATH_NAME = "large";

        public static final String TITLE = "title";

        public static final String LAST_UPDATE = "last_update";
    }
}