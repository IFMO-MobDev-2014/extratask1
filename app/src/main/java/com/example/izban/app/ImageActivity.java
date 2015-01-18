package com.example.izban.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class ImageActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    ProgressBar progressBar;
    ImageView imageView;
    MyResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("", "ImageActivity created");
        setContentView(R.layout.activity_image);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        imageView = (ImageView)findViewById(R.id.imageView);
        resultReceiver = new MyResultReceiver(new Handler());
        resultReceiver.setReceiver(this);
        Intent intent = new Intent(this, DownloadOneImageService.class).putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra("link", getIntent().getStringExtra("link"));
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case Constants.RECEIVER_STARTED:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case Constants.RECEIVER_FINISHED:
                progressBar.setVisibility(View.INVISIBLE);
                Bitmap bitmap = data.getParcelable("bitmap");
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                break;
            case Constants.RECEIVER_FAILED:
                Toast.makeText(this, "failed to refresh", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
