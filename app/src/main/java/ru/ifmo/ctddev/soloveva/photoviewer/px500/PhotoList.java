package ru.ifmo.ctddev.soloveva.photoviewer.px500;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by maria on 17.01.15.
 */
public class PhotoList {
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("total_pages")
    private int totalPages;
    private List<PhotoInfo> photos;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<PhotoInfo> getPhotos() {
        return photos;
    }
}
