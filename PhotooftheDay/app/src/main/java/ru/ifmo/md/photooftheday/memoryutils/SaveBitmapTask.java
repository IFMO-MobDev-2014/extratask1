package ru.ifmo.md.photooftheday.memoryutils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public class SaveBitmapTask extends AsyncTask<Bitmap, Void, Boolean> {
    public static final String TAG = SaveBitmapTask.class.getSimpleName();

    public static final int QUALITY = 100;

    private String[] fileNames;

    public SaveBitmapTask(String... fileNames) {
        this.fileNames = fileNames;
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {
        if (fileNames.length != bitmaps.length) {
            Log.w(TAG, fileNames.length + " != " + bitmaps.length);
        }
        if (!FilesUtils.isExternalStorageWritable()) {
            Log.e(TAG, "External storage is not writable");
            return false;
        }
        for (int i = 0; i < Math.min(fileNames.length, bitmaps.length); ++i) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmaps[i].compress(Bitmap.CompressFormat.PNG, QUALITY, bytes);
            if (FilesUtils.fileExists(FilesUtils.getApplicationStorageDir(), fileNames[i])) {
                continue;
            }
            File fullPath = FilesUtils.createFile(FilesUtils.getApplicationStorageDir(), fileNames[i]);
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(fullPath);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            try {
                outputStream.write(bytes.toByteArray());
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return true;
    }
}
