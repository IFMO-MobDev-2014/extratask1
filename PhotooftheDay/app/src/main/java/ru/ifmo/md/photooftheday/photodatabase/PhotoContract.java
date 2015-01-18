package ru.ifmo.md.photooftheday.photodatabase;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vadim on 18/01/15.
 */
public final class PhotoContract {
    public interface PhotoColumns {
        public static final String TITLE = "photo_title";
        public static final String URL_FULL = "photo_url_full";
        public static final String URL_THUMBNAIL = "photo_url_thumbnail";
        public static final String VALID_STATE = "photo_valid_state";
    }

    public static final String AUTHORITY = PhotoContract.class.getCanonicalName();

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH = "photos";

    public static final class Photo implements PhotoColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String[] ALL_COLUMNS = {
                BaseColumns._ID,
                PhotoColumns.TITLE,
                PhotoColumns.URL_FULL,
                PhotoColumns.URL_THUMBNAIL,
                PhotoColumns.VALID_STATE
        };

        public static Uri buildPhotoUri(String photoId) {
            return CONTENT_URI.buildUpon().appendPath(photoId).build();
        }
    }
}
