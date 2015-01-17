package com.example.maksim.photoview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class FullScreenImage extends ActionBarActivity {

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ImageView view = (ImageView) findViewById(R.id.imageView);
        //Bitmap largeImage = getIntent().getParcelableExtra("image");
        //view.setImageBitmap(largeImage);
        String link = getIntent().getStringExtra("link");
        try {
            URL url = new URL(link);
            url.openConnection();
            InputStream is = url.openConnection().getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);
            view.setImageBitmap(image);
            is.close();
        } catch(Exception e) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("No Internet Connection");
            dialog.show();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(FullScreenImage.this, MainActivity.class);
        startActivity(intent);
    }
}
