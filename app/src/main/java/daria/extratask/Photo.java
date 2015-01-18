package daria.extratask;

import android.graphics.Bitmap;

/**
 * Created by daria on 18.01.15.
 */
public class Photo {
    String thumbnailURL, fullImageURL;
    Bitmap thumbnail, fullImage;

    public Photo(String thumbnailURL, String fullImageURL) {
        this.thumbnailURL = thumbnailURL;
        this.fullImageURL = fullImageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getFullImageURL() {
        return fullImageURL;
    }

    public void setFullImageURL(String fullImageURL) {
        this.fullImageURL = fullImageURL;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getFullImage() {
        return fullImage;
    }

    public void setFullImage(Bitmap fullImage) {
        this.fullImage = fullImage;
    }
}
