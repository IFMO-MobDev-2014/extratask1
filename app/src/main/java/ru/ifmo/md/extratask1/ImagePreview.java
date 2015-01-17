package ru.ifmo.md.extratask1;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by anton on 17/01/15.
 */
public class ImagePreview extends Activity {
    public static final long IMAGE_PREVIEW_TIMEOUT = 4000;

    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        imgView = (ImageView) findViewById(R.id.preview_container);
        String url = getIntent().getStringExtra("url");
        url = url.replace("_M", "_orig");
        ImageCacher cacher = new ImageCacher(this);
        ImageLoadTask loadTask = new ImageLoadTask(imgView, cacher, this, url);
        TimeoutTaskRunner.runTask(loadTask, IMAGE_PREVIEW_TIMEOUT);
    }

    public void setImageView(final ImageView imageView, final Drawable d) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageDrawable(d);
            }
        });
    }
}
