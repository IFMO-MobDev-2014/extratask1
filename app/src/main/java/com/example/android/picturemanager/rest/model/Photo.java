package com.example.android.picturemanager.rest.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Photo {
    public Photo(String s) {
        try {
            //TODO check for "it's"
            JSONObject js = new JSONObject(s);
            this.name = js.getString("name");
            this.username = js.getString("username");
            this.big_image_url = js.getString("big_image_url");
            this.browser_url = js.getString("browser_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SerializedName("name")
    private String name;

    @SerializedName("user")
    private User user;

    @SerializedName("url")
    private String browser_url;

    @SerializedName("image_url")
    private String image_url;

    private String title;
    private String username;
    private String big_image_url;

    public String getImage_url() {
        return image_url;
    }

    public String getBrowser_url() {
        return browser_url;
    }

    public String getName() {
        if (name == null) {
            name = "Unnamed";
        }
        return name;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        if (username == null) {
            if (user == null) {
                username = "Unknown";
            } else {
                username = user.getUsername();
            }
        }
        return username;
    }

    public String getTitle() {
        if (title == null) {
            title = name + " by " + getUsername();
        }
        return title;
    }

    public static ArrayList<Photo> getPhotos(ArrayList<String> list) {
        ArrayList<Photo> res = new ArrayList<>();
        for (String s : list) {
            res.add(new Photo(s));
        }
        return res;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", username='" + getUsername() + '\'' +
                ", big_image_url='" + getBig_image_url() + '\'' +
                ", browser_url='" + browser_url + '\'' +
                '}';
    }

    public String getBig_image_url() {
        // SECRET CODE
        // to get the biggest image, api allows only to get sizes 1..4
        if (big_image_url == null) {
            char[] chars = image_url.toCharArray();

            int id = image_url.lastIndexOf(".jpg");
            if (id < 0) {
                return big_image_url = image_url;
            }
            chars[id - 1] = '5';
            big_image_url = new String(chars);
        }
        // SECRET CODE
        return big_image_url;
    }
}
