package com.example.maksim.photoview;

import android.graphics.Bitmap;

public class Image {

    Bitmap smallImage;
    Bitmap largeImage;
    //String linkOnLarge;

    public Image(Bitmap smallImage, Bitmap largeImage/*, String linkOnLarge*/) {
        this.smallImage = smallImage;
        this.largeImage = largeImage;
        //this.linkOnLarge = linkOnLarge;
    }

    Bitmap getSmallImage() {
        return smallImage;
    }

    Bitmap getLargeImage() {
        return largeImage;
    }

    /*String getLinkOnLarge() {
        return linkOnLarge;
    }*/
}
