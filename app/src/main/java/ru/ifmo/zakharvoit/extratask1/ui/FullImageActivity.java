package ru.ifmo.zakharvoit.extratask1.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ru.ifmo.zakharvoit.extratask1.R;
import ru.ifmo.zakharvoit.extratask1.util.StreamUtil;

public class FullImageActivity extends ActionBarActivity {

    public static final String IMAGE_TITLE_EXTRA =
            "ru.ifmo.zakharvoit.extratask1.ui.FullImageActivity.title";
    public static final String IMAGE_LINK_EXTRA =
            "ru.ifmo.zakharvoit.extratask1.ui.FullImageActivity.link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        showImage();
    }

    private void showImage() {
        final ImageView imageView = (ImageView) findViewById(R.id.full_size_image);
        final Context context = this;

        Intent intent = getIntent();
        final String title = intent.getStringExtra(IMAGE_TITLE_EXTRA);
        final String url = intent.getStringExtra(IMAGE_LINK_EXTRA);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.show();
        progress.setMessage("Loading full version, please wait...");

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    InputStream is = new URL(url).openStream();
                    byte[] value = StreamUtil.inputStreamToByteArray(is);
                    return BitmapFactory.decodeByteArray(value, 0, value.length);
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(context, "An error happened when downloading full version",
                            Toast.LENGTH_SHORT).show();
                } else {
                    imageView.setImageBitmap(bitmap);
                }

                progress.dismiss();
            }
        }.execute();
    }
}
