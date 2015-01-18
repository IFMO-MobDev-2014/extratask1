package ru.ifmo.md.extratask1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Svet on 15.01.2015.
 */
public class ImageContainer {
    View mainView;
    ImageView imageView;
    ProgressBar progressBar;
    String urlL;
    String urlXXXL;
    String title;
    String author;
    String addressL;
    String addressXXXL;
    int imageSize;

    public ImageContainer(String urlL, String urlXXXL, String title, String author, String addressL,
                          String addressXXXL, View mainView, ImageView imageView, ProgressBar progressBar) {
        this.mainView = mainView;
        this.urlL = urlL;
        this.urlXXXL = urlXXXL;
        this.title = title;
        this.author = author;
        this.addressL = addressL;
        this.addressXXXL = addressXXXL;
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    public void setImageSize(int size) {
        imageSize = size;
        progressBar.setMax(size);
        progressBar.setProgress(0);
    }

    public void increaseProgressBar(int diff) {
        progressBar.setProgress(progressBar.getProgress() + diff);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.GONE);
    }

    public void showProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setProgress(0);
    }

    public void setImage() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 4;
        Bitmap b = BitmapFactory.decodeFile(addressL, o);
        imageView.setImageBitmap(b);
    }
}
