package ru.ifmo.md.extratask1;

import android.graphics.Bitmap;

/**
 * Created by pinguinson on 16.01.2015.
 */
public class Photo {
    private String fullURL;
    private String previewURL;
    private Bitmap fullSize;
    private Bitmap preview;

    public Photo(String fullURL, String previewURL) {
        this.fullURL = fullURL;
        this.previewURL = previewURL;
    }

    public Photo(Bitmap fullSize, Bitmap preview) {
        this.fullSize = fullSize;
        this.preview = preview;
    }

    public Photo(String fullURL) {
        this.fullURL = fullURL;
    }

    public String getFullURL() {
        return fullURL;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public Bitmap getFullSize() {
        return fullSize;
    }

    public void setFullSize(Bitmap fullSize) {
        this.fullSize = fullSize;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap preview) {
        this.preview = preview;
    }
}
