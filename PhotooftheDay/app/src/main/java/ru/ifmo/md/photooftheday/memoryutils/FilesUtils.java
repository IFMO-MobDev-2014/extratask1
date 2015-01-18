package ru.ifmo.md.photooftheday.memoryutils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by vadim on 18/01/15.
 */
public class FilesUtils {
    public static final String TAG = FilesUtils.class.getSimpleName();

    private static final String APP_NAME = "Photo of the Day";
    private static File APP_DIR;

    private FilesUtils() {
    }

    public static File getApplicationStorageDir() {
        // Get the directory for the user's public pictures directory.
        if (APP_DIR == null) {
            APP_DIR = createDirectory(Environment.getExternalStorageDirectory(), APP_NAME);
        }
        return APP_DIR;
    }

    public static File createDirectory(File path, String dirName) {
        if (!path.exists()) {
            Log.e(TAG, path + " doesn't exist");
            return null;
        }
        File file = new File(path, dirName);
        if (!file.exists() && !file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    public static File createFile(File path, String fileName) {
        if (!path.exists()) {
            Log.e(TAG, path + " doesn't exist");
            return null;
        }
        File file = new File(path, fileName);
        if (file.exists()) {
            return file;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "File not created");
        }
        return file;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
