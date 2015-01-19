package ru.ifmo.instafeed.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by creed on 17.01.15.
 */
public class Meta {
    @SerializedName("code")
    public Integer code;

    //Getters

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "code=" + code +
                '}';
    }
}
