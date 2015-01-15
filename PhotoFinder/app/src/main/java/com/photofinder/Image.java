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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getXxlLink() {
        return xxlLink;
    }

    public void setXxlLink(String xxlLink) {
        this.xxlLink = xxlLink;
    }
}
