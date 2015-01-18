package com.example.izban.app;

/**
 * Created by izban on 17.01.15.
 */
public class MyImage {
    String link;
    String filePath;

    MyImage() {}

    MyImage(String link, String filePath) {
        this.link = link;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return String.format("link: %s, filePath: %s", link, filePath);
    }
}
