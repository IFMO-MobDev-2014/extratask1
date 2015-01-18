package ru.ifmo.ctddev.soloveva.photoviewer.px500;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maria on 17.01.15.
 */
public class PhotoInfo {
    private String id;
    private String name;
    private int width;
    private int height;
    @SerializedName("image_url")
    private String[] imageUrls;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }
}
