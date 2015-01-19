package ru.ifmo.instafeed.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by creed on 18.01.15.
 */
public class Image {
    @SerializedName("url")
    public String url;

    @SerializedName("width")
    public String width;

    @SerializedName("height")
    public String height;

    //Getters

    public String getUrl() {
        return url;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
