package com.example.heat_wave.photoviewer.models;

import android.graphics.Bitmap;
/**
 * Created by heat_wave on 14.01.15.
 */

public class Photo {
    private String thumbnailURL;
    private String fullURL;
    private Bitmap full;
    private Bitmap thumbnail;

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getFull() {
        return full;
    }

    public void setFull(Bitmap full) {
        this.full = full;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public Photo(String thumbnailURL, String fullURL) {
        this.thumbnailURL = thumbnailURL;
        this.fullURL = fullURL;
    }

    public String getFullURL() {
        return fullURL;
    }

    public void setFullURL(String fullURL) {
        this.fullURL = fullURL;
    }
}