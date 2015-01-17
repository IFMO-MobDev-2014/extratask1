package ru.ifmo.md.extratask1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by anton on 25/09/14.
 */
public class ImageLoadTask implements Runnable {
    Context ctx;
    private ImageView imageView;
    private ImageCacher cacher;
    private String url;

    public ImageLoadTask(ImageView imageView, ImageCacher cacher, Context ctx, String url) {
        this.imageView = imageView;
        this.cacher = cacher;
        this.ctx = ctx;
        this.url = url;
    }

    @Override
    public void run() {
        Drawable initial;

        try {
            InputStream is = (InputStream) new URL(url).getContent();
            initial = Drawable.createFromStream(is, "");
        } catch (IOException e) {
            initial = ctx.getResources().getDrawable(R.drawable.image_error);
        }

        String resId = new File(url).getName();
        if (cacher != null && !cacher.isAvailable(resId)) {
            cacher.set(resId, initial);
        }
        if (ctx instanceof ImagePreview) {
            ImagePreview activity = (ImagePreview) ctx;
            activity.setImageView(imageView, initial);
        } else if (ctx instanceof ResultsList) {
            ResultsList activity = (ResultsList) ctx;
            activity.datasetChanged();
        }
    }
}
