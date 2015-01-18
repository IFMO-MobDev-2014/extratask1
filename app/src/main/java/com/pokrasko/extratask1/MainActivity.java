package com.pokrasko.extratask1;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements ImageResultReceiver.Receiver {
    SwipeRefreshLayout layout;
    ProgressBar bar;
    ViewPager pager;

    ImageResultReceiver receiver;

    static final int AMOUNT = 100;
    static ArrayList<Bitmap> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        bar = (ProgressBar) findViewById(R.id.progress);
        bar.setMax(AMOUNT);

        pager = (ViewPager) findViewById(R.id.pager);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), 5, 2));
        } else {
            pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), 3, 4));
        }

        receiver = new ImageResultReceiver(new Handler());
        receiver.setReceiver(this);

        try {
            loadImages();
        } catch (FileNotFoundException e) {
            if (!ImageUpdater.running) {
                Intent intent = new Intent(this, ImageUpdater.class);
                intent.putExtra("receiver", receiver);
                startService(intent);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int code, Bundle bundle) {
        switch (code) {
            case ImageResultReceiver.OK:
                layout.setRefreshing(false);
                bar.setProgress(0);
                try {
                    loadImages();
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Images weren't loaded", Toast.LENGTH_SHORT).show();
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), 5, 2));
                } else {
                    pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), 3, 4));
                }
                break;
            case ImageResultReceiver.PROGRESS:
                bar.setProgress(bundle.getInt("progress"));
                break;
            case ImageResultReceiver.ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                layout.setRefreshing(false);
                bar.setProgress(0);
                break;
        }
    }

    private void update() {
        if (ImageUpdater.running) {
            return;
        }
        Intent intent = new Intent(this, ImageUpdater.class);
        intent.putExtra("receiver", receiver);
        startService(intent);
    }

    private void loadImages() throws FileNotFoundException {
        for (int i = 0; i < AMOUNT; i++) {
            Bitmap bitmap = loadImage(i);
            images.add(bitmap);
        }
    }

    private Bitmap loadImage(int index) throws FileNotFoundException {
        try {
            FileInputStream fis = this.openFileInput("preview" + index);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            return bitmap;
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
    }
}
