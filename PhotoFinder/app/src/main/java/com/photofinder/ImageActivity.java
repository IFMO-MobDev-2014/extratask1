package com.photofinder;

import android.app.LoaderManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ImageActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<ArrayList<Image>> {

    ArrayList<Boolean> used;
    private Bitmap current;
    ArrayList<Image> data = new ArrayList<>();
    Intent intent;
    int pos;
    private MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;
    IntentFilter intentFilter;
    boolean portrait;

    public Loader<ArrayList<Image>> onCreateLoader(int i, Bundle bundle) {
        return new ImagesAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Image>> listLoader, final ArrayList<Image> list) {
        data = list;
        loadHQ(pos);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Image>> listLoader) {
        new ImagesAsyncTaskLoader(this);
    }


    public class MyUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(ImageService.EXTRA_KEY_ADD);
            current = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            data.set(pos, new Image(current, data.get(pos).link, data.get(pos).xxlLink));
            used.set(pos, true);
            imageView.setImageBitmap(current);
        }
    }
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        myUpdateBroadcastReceiver = new MyUpdateBroadcastReceiver();
        intentFilter = new IntentFilter(ImageService.ACTION_ADD);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myUpdateBroadcastReceiver, intentFilter);
        portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        Intent intent = getIntent();
        pos = intent.getIntExtra("POS", 0);
        used = new ArrayList<>();
        for (int i = 0; i < getResources().getInteger(R.integer.pictures_count); i++)
            used.add(false);

        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                if (pos > 0) {
                    pos--;
                    loadHQ(pos);
                }
            }
            public void onSwipeLeft() {
                if (pos < data.size()) {
                    pos++;
                    loadHQ(pos);
                }
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    void loadHQ(Integer id) {
        current = data.get(id).bitmap;
        imageView.setImageBitmap(current);
        if (!used.get(id)) {
            intent = new Intent(ImageActivity.this, ImageService.class);
            intent.putExtra("JOB", false);
            intent.putExtra("XXL_LINK", data.get(id).xxlLink);
            intent.putExtra("ID", id);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myUpdateBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myUpdateBroadcastReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bitmap bitmap = current;
        switch (id) {
            case R.id.save:
                OutputStream outputStream;
                File file = new File(Environment.getExternalStorageDirectory().toString(), "" + Calendar.getInstance().get(Calendar.SECOND));
                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, file.getName());
                    contentValues.put(MediaStore.Images.Media.DESCRIPTION, file.getName());
                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    Toast.makeText(this, getString(R.string.save_in_gallery), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.wallpaper:
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                try {
                    wallpaperManager.setBitmap(current);
                    Toast.makeText(this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.wallpaper_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.browser:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(pos).link));
                startActivity(browserIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
