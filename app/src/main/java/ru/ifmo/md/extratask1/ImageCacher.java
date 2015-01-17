package ru.ifmo.md.extratask1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anton on 17/01/15.
 */
public class ImageCacher {
    private Context context;
    private String cacheDir;

    public ImageCacher(Context ctx) {
        context = ctx;
        cacheDir = ctx.getCacheDir().getAbsolutePath();
    }

    public boolean isAvailable(String res) {
        return getFile(res).exists();
    }

    public void set(String res, Drawable bmp) {
        File file = getFile(res);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap bitmap = ((BitmapDrawable) bmp).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            fos.write(stream.toByteArray());
            fos.close();
        } catch (IOException e) {
            // should not occur here
        }
    }

    public Drawable get(String res) {
        String filename = getFile(res).getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private File getFile(String res) {
        return new File(cacheDir + File.separator + res);
    }
}
