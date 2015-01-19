package com.pokrasko.extratask1;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullActivity extends ActionBarActivity implements ImageResultReceiver.Receiver {
    private int index;
    ProgressBar bar;
    ImageView imageView;
    Bitmap bitmap = null;

    boolean menuEnabled = false;

    String full;
    String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        imageView = (ImageView) findViewById(R.id.fullImageView);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bar = (ProgressBar) findViewById(R.id.fullProgressBar);
        bar.setMax(2);
        bar.setVisibility(View.GONE);

        index = getIntent().getIntExtra("index", 0);
        ImageResultReceiver receiver = new ImageResultReceiver(new Handler());
        receiver.setReceiver(this);

        Intent intent = new Intent(this, ImageUpdater.class);
        intent.putExtra("receiver", receiver);
        intent.putExtra("index", index);
        try {
            FileInputStream fis = this.openFileInput("full" + index);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            imageView.setImageBitmap(bitmap);

            intent.putExtra("image", false);
        } catch (Exception e) {
            intent.putExtra("image", true);
            bar.setVisibility(View.VISIBLE);
        }
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.browser_item:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(page)));
                break;
            case R.id.save_item:
                try {
                    String name = Uri.parse(full).getLastPathSegment();
                    File file = new File(Environment.getExternalStorageDirectory().toString(),
                            name + ".jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                    fos.flush();
                    fos.close();

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, name);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Toast.makeText(this, R.string.saved + name + ".jpg", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, R.string.not_saved, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wallpaper_item:
                WallpaperManager manager = WallpaperManager.getInstance(this);
                if (bitmap == null) {
                    Toast.makeText(this, R.string.wait_wallpaper, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        manager.setBitmap(bitmap);
                        Toast.makeText(this, R.string.set_wallpaper, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(this, R.string.not_set_wallpaper, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(menuEnabled);
        menu.getItem(1).setEnabled(menuEnabled);
        menu.getItem(2).setEnabled(menuEnabled);

        return true;
    }

    @Override
    public void onReceiveResult(int code, Bundle bundle) {
        switch (code) {
            case ImageResultReceiver.OK:
                try {
                    boolean image = bundle.getBoolean("image");
                    if (image) {
                        FileInputStream fis = this.openFileInput("full" + index);
                        bitmap = BitmapFactory.decodeStream(fis);
                        fis.close();
                        imageView.setImageBitmap(bitmap);
                    }

                    getSupportActionBar().setTitle(bundle.getString("title"));
                    full = bundle.getString("full");
                    page = bundle.getString("page");

                    menuEnabled = true;
                    invalidateOptionsMenu();
                } catch (IOException e) {
                    Toast.makeText(this, R.string.not_loaded, Toast.LENGTH_SHORT).show();
                }
                bar.setProgress(2);
                bar.setVisibility(View.GONE);
                break;
            case ImageResultReceiver.ERROR:
                Toast.makeText(this, R.string.not_loaded, Toast.LENGTH_SHORT).show();
                bar.setProgress(2);
                bar.setVisibility(View.GONE);
                break;
            case ImageResultReceiver.PROGRESS:
                bar.setProgress(1);
                break;
        }
    }
}
