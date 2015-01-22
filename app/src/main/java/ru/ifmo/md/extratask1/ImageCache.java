package ru.ifmo.md.extratask1;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class ImageCache {
    private HashMap<String, Bitmap> cache;

    private Context ctx;

    ImageCache(Context ctx) {
        this.ctx = ctx;
        cache = new HashMap<String, Bitmap>();
    }


    public Bitmap get(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }
        // disk
        Bitmap bm;
        try {
            bm = BitmapFactory.decodeStream(new FileInputStream(new File(ctx.getCacheDir(), "" + url.hashCode())));
        } catch (FileNotFoundException e) {
            bm = null;
        }
        if (bm != null) {
            cache.put(url, bm);
            return bm;
        }
        try {
            // network
            URL u = new URL(url);
            bm = BitmapFactory.decodeStream(u.openConnection().getInputStream());
        } catch (IOException e) {
            bm = null;
        }
        if (bm != null) {
            cache.put(url, bm);
            // save on disk
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(ctx.getCacheDir(), "" + url.hashCode()));
                bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (IOException e) {
                // ignore
                Log.w("imagecache", "cant write file " + ctx.getCacheDir().getAbsolutePath() + "/" + url.hashCode());
            }
        } else {
            Log.w("imagecache", "cant load " + url);
        }
        return bm;
    }

    public void purgeMemCache() {
        cache.clear();
    }
}
