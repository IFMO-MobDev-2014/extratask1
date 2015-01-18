package com.pokrasko.extratask1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FullActivity extends ActionBarActivity implements ImageResultReceiver.Receiver {
    private int index;
    ProgressBar bar;
    ImageView imageView;

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

        try {
            FileInputStream fis = this.openFileInput("full" + index);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Intent intent = new Intent(this, ImageUpdater.class);
            intent.putExtra("receiver", receiver);
            intent.putExtra("index", index);
            bar.setVisibility(View.VISIBLE);
            startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveResult(int code, Bundle bundle) {
        switch (code) {
            case ImageResultReceiver.OK:
                try {
                    FileInputStream fis = this.openFileInput("full" + index);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    fis.close();
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Toast.makeText(this, "Error: image isn't been loaded", Toast.LENGTH_SHORT).show();
                }
                bar.setProgress(2);
                bar.setVisibility(View.GONE);
                break;
            case ImageResultReceiver.ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                bar.setProgress(2);
                bar.setVisibility(View.GONE);
                break;
            case ImageResultReceiver.PROGRESS:
                bar.setProgress(1);
                break;
        }
    }
}
