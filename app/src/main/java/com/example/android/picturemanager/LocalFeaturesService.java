package com.example.android.picturemanager;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by lightning95 on 1/16/15.
 */

public class LocalFeaturesService extends IntentService {
    private static final String SERVICE_TITLE = "LocalFeaturesService";
    Handler handler;

    public LocalFeaturesService() {
        super(SERVICE_TITLE);
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String bigImageUrl = intent.getStringExtra("big_image_url");

        List<Bitmap> list = MemoryCacheUtils.findCachedBitmapsForImageUri(bigImageUrl,
                ImageLoader.getInstance().getMemoryCache());

        Bitmap bitmap = list.get(0);
        if (bitmap == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.toastWallpaperFailed),
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        if (intent.getAction().equals(getString(R.string.actionDownload))) {
            String root = Environment.getExternalStorageDirectory().toString();
            File image = new File(root + "/saved_images");
            image.mkdirs();

            final File file = new File(image, name + " by " + username + ".jpg");
            if (file.exists()) {
                file.delete();
//                file.renameTo(new File(image, name + " by " + username + "00.jpg"));
            }

            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Image has been saved as " + file.getName()
                            + " in " + file.getParent(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (intent.getAction().equals(getString(R.string.actionSetAsWallpaper))) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toastWallpaperSucceed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}