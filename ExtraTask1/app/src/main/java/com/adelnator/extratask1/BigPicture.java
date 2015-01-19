package com.adelnator.extratask1;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



import android.app.ActionBar;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;





import java.io.IOException;

/**
 * Created by Adel on 18.01.15.
 */

public class BigPicture extends Activity {

    Bitmap bitmap;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);
        Intent intent = getIntent();
        index = intent.getIntExtra("bitmap", 0);

        DataBase database = new DataBase(this);
        database.open();
        bitmap = database.getPicture(index);
        database.close();
        ImageView imageView = (ImageView) findViewById(R.id.pictureView);
        imageView.setImageBitmap(bitmap);
        registerForContextMenu(imageView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.pictureView:
                menu.add(0, 1, 0, "Save");
                menu.add(0, 2, 0, "Wallpaper");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                OutputStream fOut = null;
                String strDirectory = Environment.getExternalStorageDirectory().toString();
                Calendar c = Calendar.getInstance();
                int seconds = c.get(Calendar.SECOND);
                File f = new File(strDirectory, ""+seconds);
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
                    Toast.makeText(this, "Picture saved", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                return true;
            case 2:
                WallpaperManager manager = WallpaperManager.getInstance(this);
                try {
                    manager.setBitmap(bitmap);
                    Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}