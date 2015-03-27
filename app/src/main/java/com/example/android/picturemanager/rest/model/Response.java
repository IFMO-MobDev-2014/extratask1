package com.example.android.picturemanager.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {
    @SerializedName("photos")
    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    @Override
    public String toString() {
        return "{" + "photos='" + photos + "\'" + '}';
    }
}