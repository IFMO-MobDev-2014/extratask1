package com.example.maksim.photoview;

import android.graphics.Bitmap;

public class Image {

    Bitmap smallImage;

    public Image(Bitmap smallImage) {
        this.smallImage = smallImage;
    }

    Bitmap getSmallImage() {
        return smallImage;
    }

}
