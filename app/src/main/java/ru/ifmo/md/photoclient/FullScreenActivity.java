package ru.ifmo.md.photoclient;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Шолохов on 17.01.2015.
 */
public class FullScreenActivity extends ActionBarActivity implements MyResultReceiver.Receiver {

    private ImageView imageView;
    private ProgressBar progressBar;

    private MyResultReceiver receiver;
    Bitmap bmpLarge;

    private int number;
    String onSite;
    String hrefToL;
    String description = "The image from Yandex.Photo";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        receiver = new MyResultReceiver(new Handler());
        receiver.setReceiver(this);

        imageView = (ImageView)findViewById(R.id.large_imageview);
        TextView descText = (TextView)findViewById(R.id.desc_textview);

        progressBar = (ProgressBar)findViewById(R.id.fullscreen_progressbar);
        progressBar.setMax(2);
        progressBar.setVisibility(View.GONE);

        number = getIntent().getIntExtra("number", 0);

        Cursor c = getContentResolver().query(MyContentProvider.TABLE_PHOTOS_URI, null, MyContentProvider.COLUMN_PHOTO_NAME + " = '" + number + "' ", null, null, null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            onSite = c.getString(c.getColumnIndex(MyContentProvider.COLUMN_PHOTO_LINK_ONSITE));
            hrefToL = c.getString(c.getColumnIndex(MyContentProvider.COLUMN_PHOTO_LINK_LARGE));
            description = c.getString(c.getColumnIndex(MyContentProvider.COLUMN_PHOTO_TAB));
        } else {
            Toast.makeText(getApplicationContext(), "Database error", Toast.LENGTH_LONG).show();
            return;
        }

        descText.setText(description);

        c.close();
        bmpLarge = null;
        try {
            FileInputStream fis = this.openFileInput(""+(200+number));
            bmpLarge = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (Exception e) {
            Log.d("ERROR", "on Loading");
        }
        if (bmpLarge == null) {
            progressBar.setProgress(1);
            progressBar.setVisibility(View.VISIBLE);
            Log.d("IMAGE DOWNLOADING", hrefToL);
            Intent loadFeed = new Intent(FullScreenActivity.this, MyIntentService.class);
            loadFeed.putExtra("link", hrefToL);
            loadFeed.putExtra("receiver", receiver);
            loadFeed.putExtra("mode", 0);
            startService(loadFeed);
        } else {
            imageView.setImageBitmap(bmpLarge);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        if (resultCode == MyResultReceiver.FAIL) {
            Log.d("ERROR", "fail from intent service");
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
        }
        else if (resultCode == MyResultReceiver.PROGRESS) {
            progressBar.setProgress(1);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if (resultCode == MyResultReceiver.DONE) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
            bmpLarge = data.getParcelable("image");
            imageView.setImageBitmap(bmpLarge);
            saveImage(""+(200+number), bmpLarge);
        }
    }

    void saveImage(String name, Bitmap image) {
        try {
            FileOutputStream fos = this.openFileOutput(name, getApplicationContext().MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fullscreen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.browser_menuitem: {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(onSite)));
                break;
            }
            case R.id.wallpaper_menuitem: {
                WallpaperManager manager = WallpaperManager.getInstance(this);
                try {
                    if (bmpLarge == null) {
                        Toast.makeText(getApplicationContext(), "Image still loading", Toast.LENGTH_LONG).show();
                    } else {
                        manager.setBitmap(bmpLarge);
                        Toast.makeText(getApplicationContext(), "Wallpaper changed", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.save_menuitem: {
                try {
                    String name = Uri.parse(onSite).getLastPathSegment();
                    File file = new File(Environment.getExternalStorageDirectory().toString(), name + ".jpg" );
                    FileOutputStream fOut = new FileOutputStream(file);
                    bmpLarge.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
                    fOut.flush();
                    fOut.close();
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, name);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Toast.makeText(this, "Image has registered in gallery an saved as " + name + ".jpg", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Image hasn't been saved: I/O Exception", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
