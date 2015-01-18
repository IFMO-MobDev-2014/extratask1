package ru.ifmo.md.extratask1.photoclient;

import java.util.ArrayList;

/**
 * Created by sergey on 16.01.15.
 */
public class ImageFeed {

    private String title = "";
    private String nextFeedLink = "";
    private ArrayList<ImageEntry> imageEntries = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNextFeedLink() {
        return nextFeedLink;
    }

    public void setNextFeedLink(String nextFeedLink) {
        this.nextFeedLink = nextFeedLink;
    }

    public ArrayList<ImageEntry> getImageEntries() {
        return imageEntries;
    }

    public void setImageEntries(ArrayList<ImageEntry> imageEntries) {
        this.imageEntries = imageEntries;
    }
}
