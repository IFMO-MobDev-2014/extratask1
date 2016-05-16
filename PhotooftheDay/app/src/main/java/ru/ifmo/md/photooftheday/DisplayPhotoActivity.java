package ru.ifmo.md.photooftheday;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import ru.ifmo.md.photooftheday.memoryutils.FilesUtils;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public class DisplayPhotoActivity extends Activity {
    public static final String TAG = DisplayPhotoActivity.class.getSimpleName();

    private Photo photo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        photo = getIntent().getParcelableExtra(MainActivity.PHOTO);
        imageView.setImageBitmap(photo.getFullBitmap());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            File input = photo.getPathToFullBitmap();
            FileChannel src = null;
            try {
                src = new FileInputStream(input).getChannel();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!FilesUtils.isExternalStorageWritable()) {
                Log.e(TAG, "External storage is not writable");
                Toast.makeText(this, getString(R.string.photo_not_saved), Toast.LENGTH_SHORT).show();
            }

            File output = FilesUtils.createFile(path, photo.name + ".png");
            if (output == null) throw new AssertionError("bad path [" + path + "]");
            FileChannel dest = null;
            try {
                dest = new FileOutputStream(output).getChannel();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            boolean saved = false;
            if (src != null && dest != null) {
                try {
                    dest.transferFrom(src, 0, src.size());
                    saved = true;
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            if (saved) {
                Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Can not save this photo");
                Toast.makeText(this, getString(R.string.photo_not_saved), Toast.LENGTH_SHORT).show();
            }
            try {
                if (src != null) {
                    src.close();
                }
                if (dest != null) {
                    dest.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else if (id == R.id.action_set_wallpaper) {
            try {
                WallpaperManager.getInstance(getApplicationContext()).setBitmap(photo.getFullBitmap());
                Toast.makeText(this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(this, getString(R.string.wallpaper_not_set), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_open_in_browser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(photo.fullUrl.toString()));
            startActivity(browserIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DisplayPhoto Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ru.ifmo.md.photooftheday/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DisplayPhoto Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ru.ifmo.md.photooftheday/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
