package ru.ifmo.instafeed.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by creed on 17.01.15.
 */
public class Pagination {

    @SerializedName("next_url")
    public String nextUrl;

    @SerializedName("next_max_id")
    public String nextMaxId;

    //Getters

    public String getNextUrl() {
        return nextUrl;
    }

    public String getNextMaxId() {
        return nextMaxId;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "nextUrl='" + nextUrl + '\'' +
                ", nextMaxId='" + nextMaxId + '\'' +
                '}';
    }
}
