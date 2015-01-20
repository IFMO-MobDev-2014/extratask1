package ru.ya.fotki.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import ru.ya.fotki.R;
import ru.ya.fotki.database.FotkiContentProvider;
import ru.ya.fotki.database.FotkiSQLiteHelper;

public class BigPicture extends ActionBarActivity {
    DownloadManager downloadManager;
    Long downloadId;
    String yandexId;
    ImageView imageView;
    ProgressDialog progressDialog;
    String URLXL;
    String pathXL;

    BroadcastReceiver DMBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            if (downloadManager == null) downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            Cursor c = downloadManager.query(query);
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == downloadId && c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    pathXL = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    //Log.e("uriStirng:", pathXL);
                    ContentValues values = new ContentValues();
                    values.put(FotkiSQLiteHelper.COLUMN_PATH_XL, pathXL);
                    getContentResolver().update(FotkiContentProvider.FOTKI_URI, values,
                            FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?", new String[]{yandexId});
                    imageView.setImageURI(Uri.parse(pathXL));
                }
                if (progressDialog != null) progressDialog.dismiss();
            }
        }
    };

    WallpaperManager wallpaperManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("----------------------------------", "bigPicture");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_picture);

        Intent intent = getIntent();
        yandexId = intent.getStringExtra(FotkiSQLiteHelper.COLUMN_YANDEX_ID);
        String[] selectionArgs = {yandexId};
        Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null,
                FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?", selectionArgs, null);
        if (cursor == null || cursor.getCount() != 1) throw new Error();
        cursor.moveToFirst();
        pathXL = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH_XL));
        URLXL = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_URL_XL));
        imageView = (ImageView) findViewById(R.id.big_image);
        if (pathXL == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Download image");
            progressDialog.show();
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(new DownloadManager.Request(Uri.parse(URLXL)));
        } else {
            imageView.setImageURI(Uri.parse(pathXL));
        }
        wallpaperManager = WallpaperManager.getInstance(this);
//        findViewById(R.id.go_to_internet).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
//            }
//        });
//        findViewById(R.id.save_to_galery).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(pathXL));
//                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "mmmyPhoto", null);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(DMBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(DMBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_big_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.go_to_internet) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLXL));
            startActivity(browserIntent);
        }
        if (item.getItemId() == R.id.wall_paper) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(pathXL));
                Log.e("bitmap: ", "" + bitmap.getHeight());
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(BigPicture.this, "Set as wallpaper", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
