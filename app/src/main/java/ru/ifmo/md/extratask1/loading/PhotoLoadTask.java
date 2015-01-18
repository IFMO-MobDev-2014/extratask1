package ru.ifmo.md.extratask1.loading;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ru.ifmo.md.extratask1.activities.MainActivity;
import ru.ifmo.md.extratask1.activities.SinglePhotoActivity;
import ru.ifmo.md.extratask1.storage.PhotoCacher;

/**
 * Created by pinguinson on 17.01.2015.
 */
public class PhotoLoadTask implements PhotoTask {
    Context ctx;
    private PhotoCacher cacher;
    private String url;

    public PhotoLoadTask(PhotoCacher cacher, Context ctx, String url) {
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
                e.printStackTrace();
            }
        }
    }

    public void afterRun() {
        if (ctx instanceof SinglePhotoActivity) {
            SinglePhotoActivity activity = (SinglePhotoActivity) ctx;
            activity.onImageLoad();
        } else if (ctx instanceof MainActivity) {
            MainActivity activity = (MainActivity) ctx;
            activity.onPhotoLoaded();
        }
    }
}
