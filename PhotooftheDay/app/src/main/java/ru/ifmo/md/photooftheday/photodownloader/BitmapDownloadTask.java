package ru.ifmo.md.photooftheday.photodownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by vadim on 18/01/15.
 */
public class BitmapDownloadTask extends AsyncTask<URL, Void, Bitmap> {
    public static final String TAG = BitmapDownloadTask.class.getSimpleName();

    @Override
    protected Bitmap doInBackground(URL... params) {
        long startTime = System.currentTimeMillis();
        URL url = params[0];
        Log.d(TAG, "doInBackground() starts with url: '" + url + "'");
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        Log.d(TAG, "doInBackground() ready in " + (System.currentTimeMillis() - startTime) + ".ms");
        return BitmapFactory.decodeStream(inputStream);
    }
}
