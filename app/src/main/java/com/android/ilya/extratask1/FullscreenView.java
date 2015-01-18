package com.android.ilya.extratask1;

import android.app.LoaderManager;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FullscreenView extends ActionBarActivity {
    List<Bitmap> lst = new ArrayList<>();
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        // Loop through the ids to create a list of full screen image views
        List<ImageView> images = new ArrayList<>();
        for (Integer i = 0; i < 6; i++) {
            ImageView imageView = new ImageView(this);
            byte[] byteArray = getIntent().getByteArrayExtra(i.toString());
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            lst.add(bmp);
            imageView.setImageBitmap(bmp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            images.add(imageView);
        }

        // Finally create the adapter
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(images);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(imagePagerAdapter);

        // Set the ViewPager to point to the selected image from the previous activity
        // Selected image id
        int position = getIntent().getExtras().getInt("position");
        viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_saving) {
            MediaStore.Images.Media.insertImage(getContentResolver(), lst.get(viewPager.getCurrentItem()), "photofromflickr.png" , "");
            Toast toast = Toast.makeText(this, "Save photo successfully!", Toast.LENGTH_LONG);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
