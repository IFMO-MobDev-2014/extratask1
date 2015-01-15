package com.photofinder;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImageActivity extends ActionBarActivity {


    ArrayList<Bitmap> bitmapArrayList;
    ArrayList<Boolean> used;
    ArrayList<Pair<String, String>> links;
    private static Bitmap current;
    Intent intent;
    int pos;
    private MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;
    IntentFilter intentFilter;
    DataBaseAdapter dataBaseAdapter;
    boolean portrait;


    public class MyUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(ImageService.EXTRA_KEY_ADD);
            current = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bitmapArrayList.set(pos, current);
            dataBaseAdapter.open();
            dataBaseAdapter.changePicById(pos, current, links.get(pos).first, links.get(pos).second);
            dataBaseAdapter.close();
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
        dataBaseAdapter = new DataBaseAdapter(this);
        dataBaseAdapter.open();
        bitmapArrayList = dataBaseAdapter.getAllPics();
        links = dataBaseAdapter.getAllLinks();
        dataBaseAdapter.close();
        pos = intent.getIntExtra("POS", 0);
        current = bitmapArrayList.get(pos);
        used = new ArrayList<>();
        for (int i = 0; i < bitmapArrayList.size(); i++)
            used.add(false);
        imageView.setImageBitmap(current);
        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                if (pos > 0) {
                    pos--;
                    loadHQ(pos);
                }
            }

            public void onSwipeLeft() {
                if (pos < bitmapArrayList.size()) {
                    pos++;
                    loadHQ(pos);
                }
            }



        });
        loadHQ(pos);
    }

    void loadHQ(Integer id) {
        current = bitmapArrayList.get(id);
        imageView.setImageBitmap(current);
        if (!used.get(id)) {
            intent = new Intent(ImageActivity.this, ImageService.class);
            intent.putExtra("JOB", false);
            intent.putExtra("XXL_LINK", links.get(id).second);
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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(pos).first));
                startActivity(browserIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
