package ru.ifmo.md.extratask1;

import android.net.Uri;
import android.provider.BaseColumns;

public class Tables {
    public static final String AUTHORITY =
            "ru.ifmo.md.extratask1.provider.image";

    public static final class Images implements BaseColumns {
//        public static final int ID_COLUMN = 0;
//        public static final int SMALL_COLUMN = 1;
//        public static final int LARGE_URL_COLUMN = 2;
//        public static final int ORIG_URL_COLUMN = 3;
//        public static final int LARGE_COLUMN = 4;
//        public static final int LAST_UPD_COLUMN = 5;

        private Images() {}

        public static final String PATH = "image";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + Images.PATH);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.image.data";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.image.data";

        public static final String SMALL_NAME = "small";

        public static final String LARGE_URL_NAME = "large_url";

        public static final String ORIG_URL_NAME = "orig";

        public static final String LARGE_NAME = "large";

        public static final String LAST_UPD_NAME = "last_update";
    }
}

