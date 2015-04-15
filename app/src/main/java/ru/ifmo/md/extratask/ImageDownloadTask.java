package ru.ifmo.md.extratask;


import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by gshark on 15.03.15
 */
class ImageDownloadTask extends AsyncTask<String, Void, Void> {
    public static final String LOG_TAG = ImageDownloadTask.class.getSimpleName();
    public static final String WRITE_ERROR = "Couldn't save image";

    private final File file;
    private final Runnable runnable;

    public ImageDownloadTask(File file, Runnable runnable) {
        this.file = file;
        this.runnable = runnable;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        runnable.run();
    }

    @Override
    protected Void doInBackground(String... urls) {
        if (urls.length < 1) {
            throw new IllegalArgumentException();
        }
        String url = urls[0];
        try {
            URL imageUrl = new URL(url);
            URLConnection connection = imageUrl.openConnection();
            InputStream is = connection.getInputStream();

            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, WRITE_ERROR);
        }
        return null;
    }

}