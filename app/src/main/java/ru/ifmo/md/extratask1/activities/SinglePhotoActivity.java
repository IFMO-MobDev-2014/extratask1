package ru.ifmo.md.extratask1.activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import ru.ifmo.md.extratask1.storage.PhotoCacher;
import ru.ifmo.md.extratask1.loading.PhotoLoadTask;
import ru.ifmo.md.extratask1.R;
import ru.ifmo.md.extratask1.loading.TimeoutTaskRunner;

/**
 * Created by pinguinson on 17.01.2015.
 */
public class SinglePhotoActivity extends ActionBarActivity {
    public static final long IMAGE_PREVIEW_TIMEOUT = 60 * 1000;

    ImageView imageView;
    String resourceId;
    PhotoCacher cacher;
    String url;

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        imageView = (ImageView) findViewById(R.id.preview_container);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        url = getIntent().getStringExtra("url");
        resourceId = new File(url).getName();
        cacher = new PhotoCacher(this);

        PhotoLoadTask loadTask = new PhotoLoadTask(cacher, this, url);
        TimeoutTaskRunner.runTask(loadTask, IMAGE_PREVIEW_TIMEOUT);
    }

    public void onImageLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cacher.isAvailable(resourceId)) {
                    cacher.putToImageView(imageView, resourceId);
                } else {
                    imageView.setImageResource(R.drawable.image_error);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_save) {
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File from = new File(getCacheDir() + File.separator + resourceId);
                File to = new File(sdCard.getAbsolutePath() + File.separator + resourceId + ".jpg");
                copyFile(from, to);
                Toast.makeText(this, "Photo saved to SD card", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Could not save: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_set_wallpaper) {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            try {
                manager.setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                Toast.makeText(this, "Wallpaper has been changed", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.action_open_browser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
