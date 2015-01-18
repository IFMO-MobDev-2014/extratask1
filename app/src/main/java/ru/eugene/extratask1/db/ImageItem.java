package ru.eugene.extratask1.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by eugene on 1/17/15.
 */
public class ImageItem implements Serializable {
    private String imageId = "";
    private String thumbnailUrl = "";
    private String thumbnail = "";
    private String bigImageUrl = "";
    private String bigImage = "";

    public ImageItem() {
    }

    public ImageItem(String imageId, String thumbnailUrl, String thumbnail, String bigImageUrl, String bigImage) {
        this.imageId = imageId;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnail = thumbnail;
        this.bigImageUrl = bigImageUrl;
        this.bigImage = bigImage;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    public ContentValues generateContentValues() {
        ContentValues value = new ContentValues();
        value.put(ImageDataSource.COLUMN_IMAGE_ID, imageId);

        value.put(ImageDataSource.COLUMN_THUMBNAIL_URL, thumbnailUrl);
        value.put(ImageDataSource.COLUMN_THUMBNAIL, thumbnail);

        value.put(ImageDataSource.COLUMN_BIG_IMAGE_URL, bigImageUrl);
        value.put(ImageDataSource.COLUMN_BIG_IMAGE, bigImage);
        return value;
    }
}
