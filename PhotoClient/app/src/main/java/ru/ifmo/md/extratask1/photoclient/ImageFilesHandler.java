package ru.ifmo.md.extratask1.photoclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sergey on 17.01.15.
 */
public class ImageFilesHandler {

    private static final String LOG_TAG = "ru.ifmo.md.extratask1.ImageFilesHandler.Log";

    public static void downloadAndSaveImage(Context context, String imageURL) throws IOException {
        byte[] bytes = loadImageBytes(imageURL);
        if (bytes == null)
            return;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        String imageName = getFileName(imageURL);
        saveImageOnStorage(context, bitmap, imageName);
    }

    public static byte[] loadImageBytes(String imageURL) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageURL);
            connection = (HttpURLConnection) url.openConnection();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public static Bitmap loadImageFromStorage(Context context, String imageUrl)  {
        File directory = getImagesFolder(context);
        String imageName = getFileName(imageUrl);
        File path = new File(directory, imageName);

        if (!path.exists() || path.isDirectory()) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeFile(path.getAbsolutePath(), options);
    }

    public static boolean imageFileExists(Context context, String imageURL) {
        File directory = getImagesFolder(context);
        String imageName = getFileName(imageURL);
        File path = new File(directory, imageName);
        return path.exists() && path.isFile();
    }

    public static void saveImageOnStorage(Context context, Bitmap bitmap, String imageName) throws IOException {
        File directory = getImagesFolder(context);
        File path = new File(directory, imageName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getImagesFolder(Context context) {
        File imagesDir = null;
        if (isExternalStorageWritable()) {
            imagesDir = new File(Environment.getExternalStorageDirectory(), "images_folder");
            if (!imagesDir.isDirectory()) {
                if (!imagesDir.mkdirs()) {
                    Log.e(LOG_TAG, "Directory not created");
                }
            }
        }
        if (imagesDir == null || !imagesDir.isDirectory()) {
            imagesDir = context.getFilesDir();
        }
        return imagesDir;
    }

    public static String getFileName(String imageURL) {
        int lastIndexOfSlash = imageURL.lastIndexOf('/');
        return imageURL.substring(lastIndexOfSlash + 1);
    }

}
