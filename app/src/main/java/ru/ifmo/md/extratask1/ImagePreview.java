package ru.ifmo.md.extratask1;

import android.content.Intent;
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

public class ImagePreview extends ActionBarActivity {
    public static final long IMAGE_PREVIEW_TIMEOUT = 60 * 1000;

    ImageView imgView;
    String resId;
    ImageCacher cacher;

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
        setContentView(R.layout.activity_image_preview);

        Intent intent = getIntent();
        imgView = (ImageView) findViewById(R.id.preview_container);
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        url = url.replace("_M", "_XXL");
        resId = new File(url).getName();
        cacher = new ImageCacher(this);

        ImageLoadTask loadTask = new ImageLoadTask(cacher, this, url);
        TimeoutTaskRunner.runTask(loadTask, IMAGE_PREVIEW_TIMEOUT);

        setTitle(title);
    }

    public void onImageLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cacher.isAvailable(resId)) {
                    cacher.putToImageView(imgView, resId);
                } else {
                    imgView.setImageResource(R.drawable.image_error);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_save) {
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File from = new File(getCacheDir() + File.separator + resId);
                File to = new File(sdCard.getAbsolutePath() + File.separator + resId + ".jpg");
                copyFile(from, to);
                Toast.makeText(this, "Photo saved on SD card", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Could not save: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
