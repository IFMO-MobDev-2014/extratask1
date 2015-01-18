package me.loskutov.popularphotosviewer;


import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ignat on 15.01.15.
 */
class ImageDownloadTask extends AsyncTask<Void, Void, Void> {
    private final String url;
    private final File file;
    private final Runnable runnable;

    ImageDownloadTask(String url, File file, Runnable runnable) {
        this.url = url;
        this.file = file;
        this.runnable = runnable;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        runnable.run();
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        try {
            URL imageUrl = new URL(url);
            URLConnection connection = imageUrl.openConnection();
            InputStream is = connection.getInputStream();

            OutputStream os = new FileOutputStream(file);
            byte [] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}