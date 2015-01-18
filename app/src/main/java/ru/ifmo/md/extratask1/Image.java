package ru.ifmo.md.extratask1;

import android.net.Uri;
import android.provider.BaseColumns;

public class Image {
    public static final String AUTHORITY =
            "ru.ifmo.md.extratask1.provider.image";

    public static final class JustImage implements BaseColumns {
        public static final int ID_COLUMN = 0;
        public static final int SMALL_COLUMN = 1;
        public static final int LARGE_COLUMN = 2;

        private JustImage() {}

        public static final String PATH = "image";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + JustImage.PATH);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.image.data";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.image.data";

        public static final String SMALL_NAME = "small";

        public static final String LARGE_NAME = "large";
    }
}

