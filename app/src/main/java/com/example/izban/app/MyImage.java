package com.example.izban.app;

/**
 * Created by izban on 17.01.15.
 */
public class MyImage {
    String link;
    String filePath;
    int ind;
    String title;

    MyImage() {}

    MyImage(String link, String filePath, int ind, String title) {
        this.link = link;
        this.filePath = filePath;
        this.ind = ind;
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("link: %s, filePath: %s, ind: %d, title: %s", link, filePath, ind, title);
    }
}
