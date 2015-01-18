package com.example.heat_wave.photoviewer;

/**
 * Created by heat_wave on 18.01.15.
 */
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.heat_wave.photoviewer.database.PhotoDatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class PhotoSlideActivity extends FragmentActivity {

    private static final int NUM_PAGES = 20;

    private ViewPager mPager;
    private LayoutInflater mLayoutInflater;
    private PagerAdapter mPagerAdapter;
    ArrayList<Bitmap> fullPhotos;
    PhotoDatabaseHelper db;
    Bitmap bitmap;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_slide);

        db = new PhotoDatabaseHelper(this);
        fullPhotos = new ArrayList<Bitmap>();
        for (int i = 1; i <= 20; i++) {
            fullPhotos.add(db.getImageFull(i));
        }
        position = getIntent().getIntExtra("POSITION", 0);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PhotoPagerAdapter(this);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position);
        bitmap = fullPhotos.get(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_download:
                OutputStream fOut = null;
                String strDirectory = Environment.getExternalStorageDirectory().toString();
                Calendar c = Calendar.getInstance();
                int seconds = c.get(Calendar.SECOND);
                File f = new File(strDirectory, "" + seconds);
                try {
                    fOut = new FileOutputStream(f);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, f.getName());
                    values.put(MediaStore.Images.Media.DESCRIPTION, f.getName());
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Toast.makeText(this, "Image has been saved in gallery", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_set_wallpaper:
                WallpaperManager manager = WallpaperManager.getInstance(this);
                try {
                    manager.setBitmap(bitmap);
                    Toast.makeText(this, "Wallpaper has been changed", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_open_browser:
                String link = db.getLink(position + 1);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PhotoPagerAdapter extends PagerAdapter {
        Context context;

        PhotoPagerAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return fullPhotos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageBitmap(fullPhotos.get(position));
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}