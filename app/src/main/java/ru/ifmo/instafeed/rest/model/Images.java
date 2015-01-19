package ru.ifmo.instafeed.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by creed on 17.01.15.
 */
public class Images {
    @SerializedName("low_resolution")
    public Image lowRresolution;

    @SerializedName("thumbnail")
    public Image thumbnail;

    @SerializedName("standard_resolution")
    public Image standardResolution;

    //Getters

    public Image getLowRresolution() {
        return lowRresolution;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public Image getStandardResolution() {
        return standardResolution;
    }

    @Override
    public String toString() {
        return "Images{" +
                "lowRresolution=" + lowRresolution +
                ", thumbnail=" + thumbnail +
                ", standardResolution=" + standardResolution +
                '}';
    }
}
