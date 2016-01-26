package ru.ifmo.md.photooftheday.memoryutils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public class FilesUtils {
    public static final String TAG = FilesUtils.class.getSimpleName();

    private static final String APP_NAME = "Photo of the Day";
    private static File APP_DIR;

    private FilesUtils() {
    }

    public static File getApplicationStorageDir() {
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

    public static boolean fileExists(File path, String fileName) {
        if (!path.exists()) {
            Log.e(TAG, path + " doesn't exist");
            return false;
        }
        File file = new File(path, fileName);
        return file.exists();
    }

    public static File createFile(File path, String fileName) {
        if (!path.exists()) {
            Log.e(TAG, path + " doesn't exist");
            return null;
        }
        File file = new File(path, fileName);
        try {
            boolean newFile = file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "File not created");
        }
        return file;
    }

    public static boolean removeFile(File path, String fileName) {
        if (!path.exists()) {
            Log.e(TAG, path + " doesn't exist");
            return false;
        }
        File file = new File(path, fileName);
        if (file.exists()) {
            return file.delete();
        }
        Log.e(TAG, "File doesn't exist");
        return false;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
