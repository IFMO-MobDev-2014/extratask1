package com.example.android.picturemanager.rest.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    private String username;

    public String getUsername() {
        if (username == null) {
            return "Unknown";
        }
        return username;
    }

    @Override
    public String toString() {
        return "{username='" + username + "\'" + '}';
    }
}
