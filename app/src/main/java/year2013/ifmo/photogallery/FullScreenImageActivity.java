package year2013.ifmo.photogallery;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FullScreenImageActivity extends ActionBarActivity {
    public static final String EXTRA_LARGE = "extra_large";
    public static final String EXTRA_ORIG = "extra_orig";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITLE = "extra_title";

    private ImageView imageView;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        String largeUrl = getIntent().getStringExtra(EXTRA_LARGE);
        long id = getIntent().getLongExtra(EXTRA_ID, 1);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        IntentFilter mStatusIntentFilter = new IntentFilter(
                ImageIntentService.BROADCAST_LARGE);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(responseReceiver, mStatusIntentFilter);

        Uri uri = ContentUris.withAppendedId(Gallery.Images.CONTENT_URI, id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(Gallery.Images.LARGE_PATH_NAME));

        try {
            File f = new File(path, title + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Intent intent = new Intent(this, ImageIntentService.class);
            intent.setAction(ImageIntentService.ACTION_LARGE);
            intent.putExtra(ImageIntentService.EXTRA_LARGE, largeUrl);
            intent.putExtra(ImageIntentService.EXTRA_TITLE, title);
            intent.putExtra(ImageIntentService.EXTRA_ID, id);
            startService(intent);
        }
    }

    public void saveOrig(View view) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String origUrl = getIntent().getStringExtra(EXTRA_ORIG);
        long id = getIntent().getLongExtra(EXTRA_ID, 1);
        Uri uri = ContentUris.withAppendedId(Gallery.Images.CONTENT_URI, id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        Intent intent = new Intent(this, ImageIntentService.class);
        intent.setAction(ImageIntentService.ACTION_ORIG);
        intent.putExtra(ImageIntentService.EXTRA_TITLE, title);
        intent.putExtra(ImageIntentService.EXTRA_ORIG, origUrl);
        intent.putExtra(ImageIntentService.EXTRA_ID, id);
        startService(intent);
    }

    public void openInBrowser(View view) {
        String origUrl = getIntent().getStringExtra(EXTRA_ORIG);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(origUrl));
        startActivity(browserIntent);
    }

    public void setAsWallpaper(View view) {
        long id = getIntent().getLongExtra(EXTRA_ID, 1);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File directory = new File(root + "/PhotoGallery");
        //directory.mkdirs();
        try {
            File f = new File(directory, title + ".jpg");
            Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
            WallpaperManager manager = WallpaperManager.getInstance(this);
            manager.setBitmap(image);
            showToast(getString(R.string.done));
        } catch (FileNotFoundException e) {
            String origUrl = getIntent().getStringExtra(EXTRA_ORIG);
            Uri uri = ContentUris.withAppendedId(Gallery.Images.CONTENT_URI, id);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            Intent intent = new Intent(this, ImageIntentService.class);
            intent.setAction(ImageIntentService.ACTION_SET_WALLPAPER);
            intent.putExtra(ImageIntentService.EXTRA_ORIG, origUrl);
            intent.putExtra(ImageIntentService.EXTRA_TITLE, title);
            intent.putExtra(ImageIntentService.EXTRA_ID, id);
            startService(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {}

        public void onReceive(Context context, Intent intent) {
            Uri uri = ContentUris.withAppendedId(Gallery.Images.CONTENT_URI,
                    getIntent().getLongExtra(EXTRA_ID, 1));
            String title = getIntent().getStringExtra(EXTRA_TITLE);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndex(Gallery.Images.LARGE_PATH_NAME));
                try {
                    File f = new File(path, title + ".jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FullScreenImageActivity.this,
                        text,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

}
