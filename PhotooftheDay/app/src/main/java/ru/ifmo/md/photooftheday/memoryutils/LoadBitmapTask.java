package ru.ifmo.md.photooftheday.memoryutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public class LoadBitmapTask extends AsyncTask<Void, Void, Bitmap> {
    public static final String TAG = LoadBitmapTask.class.getSimpleName();

    private final String fileName;

    public LoadBitmapTask(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if (!FilesUtils.isExternalStorageReadable()) {
            Log.e(TAG, "External storage is not readable");
            return null;
        }
        File fullPath = FilesUtils.createFile(FilesUtils.getApplicationStorageDir(), fileName);
        if (fullPath == null) throw new AssertionError("there is no application dir?oO");
        return BitmapFactory.decodeFile(fullPath.getAbsolutePath());
    }
}
