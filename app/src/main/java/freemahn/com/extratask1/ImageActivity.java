package freemahn.com.extratask1;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by Freemahn on 16.01.2015.
 */
public class ImageActivity extends Activity {

    ImageView iw;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image_big);
        iw = (ImageView) findViewById(R.id.imageView);
        registerForContextMenu(iw);
        iw.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Context Menu");
                menu.add(0, v.getId(), 0, "Save photo to gallery");
                menu.add(0, v.getId(), 0, "Set as wallpaper");
            }
        });
        Intent intent = getIntent();

        String lnk = intent.getStringExtra("link");
       // Log.d("IMAGEACTIVITY", lnk + "");
        new DownloadBigImageTask(this).execute(lnk);


        //iw.setImageBitmap(bm);
    }

    void setImageBitmap(Bitmap bitmap) {
        bm = bitmap;
        iw.setImageBitmap(bm);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Save photo to gallery") {

            OutputStream fOut = null;
            String strDirectory = Environment.getExternalStorageDirectory().toString();
            int seconds = Calendar.getInstance().get(Calendar.SECOND);
            File f = new File(strDirectory, "" + seconds);
            try {
                fOut = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, f.getName());
                values.put(MediaStore.Images.Media.DESCRIPTION, f.getName());
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Toast.makeText(this, "Saving to gallery:success", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Saving to gallery:ERROR", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getTitle() == "Set as wallpaper") {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            try {
                manager.setBitmap(bm);
                Toast.makeText(this, "Setting as wallpaper:success", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Setting as wallpaper:ERROR", Toast.LENGTH_SHORT).show();
            }
        } else {
            return false;
        }
        return true;
    }
}
