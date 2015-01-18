package ru.ifmo.md.flickrclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by sultan on 18.01.15.
 */
public class ImageFileHelper {

    public static void saveImageToExternalStorage(Bitmap image, String id) {
        File file = generateFileFromId(id);
        if (file.exists ()) {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }

    public static Bitmap readImageFromExternalStorage(String id) throws FileNotFoundException {
        File file = generateFileFromId(id);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        return  bitmap;
    }

    private static File generateFileFromId(String id) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/most_recent_images");
        myDir.mkdirs();
        String fname = "image_"+ id +".png";
        File file = new File (myDir, fname);

        return file;
    }
}
