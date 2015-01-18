package year2013.ifmo.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Юлия on 18.01.2015.
 */
public class ImageService {

    public static byte[] TranslateBitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public static Bitmap GetImageByUrl(String url)
    {
        Bitmap bmp = null;
        try {
            InputStream in = new URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            // log error
        }

        return bmp;
    }
}
