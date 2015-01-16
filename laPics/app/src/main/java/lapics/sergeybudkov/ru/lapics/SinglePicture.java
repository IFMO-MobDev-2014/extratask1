package lapics.sergeybudkov.ru.lapics;

import android.graphics.Bitmap;

public class SinglePicture {
    private int width;
    private int height;
    private Bitmap bigImage;
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Bitmap getBigImage() {
        return bigImage;
    }

    public SinglePicture(int w, int h, Bitmap pics) {
        this.width = w;
        this.height = h;
        this.bigImage = pics;
    }
}