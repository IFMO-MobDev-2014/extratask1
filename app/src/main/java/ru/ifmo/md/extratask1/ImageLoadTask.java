package ru.ifmo.md.extratask1;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoadTask implements ImageTask {
    Context ctx;
    private ImageCacher cacher;
    private String url;

    public ImageLoadTask(ImageCacher cacher, Context ctx, String url) {
        this.cacher = cacher;
        this.ctx = ctx;
        this.url = url;
    }

    @Override
    public void run() {
        String resId = new File(url).getName();
        if (cacher != null && !cacher.isAvailable(resId)) {
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                cacher.set(resId, is);
            } catch (IOException e) {
                // just don't store anything in cache
            }
        }
    }

    public void afterRun() {
        if (ctx instanceof ImagePreview) {
            ImagePreview activity = (ImagePreview) ctx;
            activity.onImageLoad();
        } else if (ctx instanceof ResultsList) {
            ResultsList activity = (ResultsList) ctx;
            activity.onImageLoad();
        }
    }
}
