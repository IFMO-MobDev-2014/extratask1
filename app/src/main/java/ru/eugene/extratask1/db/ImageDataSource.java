package ru.eugene.extratask1.db;

import android.database.Cursor;

/**
 * Created by eugene on 1/17/15.
 */
public class ImageDataSource {
    public static final String TABLE_NAME = "images";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGE_ID = "image_id";
    public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
    public static final String COLUMN_THUMBNAIL = "thumbnail";
    public static final String COLUMN_BIG_IMAGE_URL = "big_image_url";
    public static final String COLUMN_BIG_IMAGE = "big_image";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME + " (" +
            COLUMN_ID + " integer primary key autoincrement" +
            ", " + COLUMN_THUMBNAIL_URL + " text not null" +
            ", " + COLUMN_THUMBNAIL + " text" +
            ", " + COLUMN_BIG_IMAGE_URL + " text not null" +
            ", " + COLUMN_BIG_IMAGE + " text not null" +
            ", " + COLUMN_IMAGE_ID + " text unique not null);";

    public static String[] getProjection() {
        return new String[]{
                COLUMN_ID, COLUMN_IMAGE_ID,
                COLUMN_THUMBNAIL_URL, COLUMN_THUMBNAIL,
                COLUMN_BIG_IMAGE_URL, COLUMN_BIG_IMAGE};
    }

    public static ImageItem generateImageItem(Cursor data) {
        String imageId = data.getString(data.getColumnIndex(COLUMN_IMAGE_ID));
        String thumbnailUrl = data.getString(data.getColumnIndex(COLUMN_THUMBNAIL_URL));
        String thumbnail = data.getString(data.getColumnIndex(COLUMN_THUMBNAIL));
        String bigImageUrl = data.getString(data.getColumnIndex(COLUMN_BIG_IMAGE_URL));
        String bigImage = data.getString(data.getColumnIndex(COLUMN_BIG_IMAGE));
        return new ImageItem(imageId, thumbnailUrl, thumbnail, bigImageUrl, bigImage);
    }
}
