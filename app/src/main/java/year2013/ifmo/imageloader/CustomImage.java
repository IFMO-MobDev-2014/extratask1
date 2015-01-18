package year2013.ifmo.imageloader;

import android.graphics.Bitmap;

/**
 * Created by Юлия on 18.01.2015.
 */
public class CustomImage {

    public int Id;
    public String SmallImageLink;
    public String BigImageLink;
    public android.graphics.Bitmap Bitmap;

    private Bitmap bmp;

    public CustomImage(String small, String big, Bitmap bmp) {
        SmallImageLink = small;
        BigImageLink = big;
        Bitmap = bmp;
    }
}

