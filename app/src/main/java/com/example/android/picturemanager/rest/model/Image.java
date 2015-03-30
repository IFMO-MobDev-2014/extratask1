package com.example.android.picturemanager.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lightning95 on 3/25/15.
 */
public class Image {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "{url='" + url + '\'' + '}';
    }
}
