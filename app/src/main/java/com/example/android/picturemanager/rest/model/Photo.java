package com.example.android.picturemanager.rest.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Photo {

    public Photo(String s) {
        try {
            //TODO check for "it's"
            s = s.replace("'s", "bloblo");
            JSONObject js = new JSONObject(s);

            this.name = js.getString("name");
            name = name.replace("bloblo", "'s");

            this.username = js.getString("username");
            username = username.replace("bloblo", "'s");

            this.browser_url = js.getString("browser_url");
            this.big_image_url = js.getString("big_image_url");
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
    }

    @SerializedName("name")
    private String name;

    @SerializedName("user")
    private User user;

    @SerializedName("url")
    private String browser_url;

    @SerializedName("images")
    private Image[] images;

    private String image_url;
    private String big_image_url;

    private String title;
    private String username;

    public String getImage_url() {
        if (image_url == null) {
            image_url = images[0].getUrl();
        }
        return image_url;
    }

    public String getBig_image_url() {
        return big_image_url;
    }

    public void setBig_image_url(String url) {
        big_image_url = url;
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
            title = getName() + " by " + getUsername();
        }
        return title;
    }

    public static List<Photo> getPhotos(List<String> list) {
        List<Photo> res = new ArrayList<>();
        for (String s : list) {
            res.add(new Photo(s));
        }
        return res;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + getName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", browser_url='" + browser_url + '\'' +
                ", big_image_url='" + getBig_image_url() + '\'' +
                '}';
    }
}
