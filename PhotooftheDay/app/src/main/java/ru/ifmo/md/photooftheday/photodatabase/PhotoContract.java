package ru.ifmo.md.photooftheday.photodatabase;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public final class PhotoContract {
    public interface PhotoColumns {
        String ID = "photo_title";
        String NAME = "photo_name";
        String URL_FULL = "photo_url_full";
        String URL_THUMBNAIL = "photo_url_thumbnail";
        String VALID_STATE = "photo_valid_state";
    }

    public static final String AUTHORITY = PhotoContract.class.getCanonicalName();

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH = "photos";

    public static final class Photo implements PhotoColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String[] ALL_COLUMNS = {
                Photo._ID,
                Photo.ID,
                Photo.NAME,
                Photo.URL_FULL,
                Photo.URL_THUMBNAIL,
                Photo.VALID_STATE
        };

        public static Uri buildPhotoUri(String photoId) {
            return CONTENT_URI.buildUpon().appendPath(photoId).build();
        }
    }
}
