package ru.ifmo.instafeed.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by creed on 17.01.15.
 */
public class Response {

    @SerializedName("pagination")
    public Pagination pagination;

    @SerializedName("meta")
    public Meta meta;

    @SerializedName("data")
    public List<Item> data;

    //Getters

    public List<Item> getData() {
        return this.data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "Response{" +
                "pagination=" + pagination +
                ", meta=" + meta +
                ", data=" + data +
                '}';
    }
}
