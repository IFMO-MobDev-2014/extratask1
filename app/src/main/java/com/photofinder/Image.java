package com.photofinder;

import android.graphics.Bitmap;

public class Image {
    Bitmap bitmap;
    String link;
    String xxlLink;

    public Image(Bitmap bitmap, String link, String xxlLink) {
        this.bitmap = bitmap;
        this.link = link;
        this.xxlLink = xxlLink;
    }
}
