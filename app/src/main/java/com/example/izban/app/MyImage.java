package com.example.izban.app;

/**
 * Created by izban on 17.01.15.
 */
public class MyImage {
    String link;
    String filePath;
    int ind;

    MyImage() {}

    MyImage(String link, String filePath, int ind) {
        this.link = link;
        this.filePath = filePath;
        this.ind = ind;
    }

    @Override
    public String toString() {
        return String.format("link: %s, filePath: %s, ind: %d", link, filePath, ind);
    }
}
