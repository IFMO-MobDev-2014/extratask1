package ru.ifmo.instafeed.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by creed on 17.01.15.
 */


public class Item
{
    @SerializedName("type")
    public String type;

    @SerializedName("images")
    public Images images;

    @SerializedName("link")
    public String link;

    //Getters

    public Images getImages() {
        return this.images;
    }

    public String getLink() {
        return link;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "type='" + type + '\'' +
                ", images=" + images +
                ", link='" + link + '\'' +
                '}';
    }
}
