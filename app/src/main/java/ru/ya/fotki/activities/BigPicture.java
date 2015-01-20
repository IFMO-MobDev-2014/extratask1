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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

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
    String URIS;
    String URIXL;

    BroadcastReceiver DMBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadIdNew = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            downloadId = (long)-1;
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadIdNew);
            if (downloadManager == null) downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            Cursor c = downloadManager.query(query);
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == downloadIdNew && c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    URIXL = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    ContentValues values = new ContentValues();
                    values.put(FotkiSQLiteHelper.COLUMN_PATH_XL, URIXL);
                    getContentResolver().update(FotkiContentProvider.FOTKI_URI, values,
                            FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?", new String[]{yandexId});
                    imageView.setImageURI(Uri.parse(URIXL));
                }
                if (progressDialog != null) progressDialog.dismiss();
            }
        }
    };

    WallpaperManager wallpaperManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadId = (long) -1;
        setContentView(R.layout.activity_big_picture);

        Intent intent = getIntent();
        yandexId = intent.getStringExtra(FotkiSQLiteHelper.COLUMN_YANDEX_ID);
        String[] selectionArgs = {yandexId};
        Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null,
                FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?", selectionArgs, null);
        if (cursor == null || cursor.getCount() != 1) throw new Error();
        cursor.moveToFirst();
        URIXL = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH_XL));
        URIS = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH_S));
        URLXL = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_URL_XL));
        cursor.close();
        imageView = (ImageView) findViewById(R.id.big_image);
        if (URIXL == null) {
            imageView.setImageURI(Uri.parse(URIS));
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Download image");
            progressDialog.show();
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue( new  DownloadManager.Request(Uri.parse(URLXL)).
                         setDestinationInExternalFilesDir(BigPicture.this, Environment.DIRECTORY_DOWNLOADS, "picture.jpg"));
            Log.e("add:", "to download manager:  " + downloadId);
        } else {
            imageView.setImageURI(Uri.parse(URIXL));
        }
        wallpaperManager = WallpaperManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(DMBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(DMBroadcastReceiver);
        if (downloadId != -1 && downloadManager != null) {
            Log.e("remove download id:", "" + downloadId);
            downloadManager.remove(downloadId);
        }
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(URIXL));
                Log.e("bitmap: ", "" + bitmap.getHeight());
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(BigPicture.this, "Set as wallpaper", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (item.getItemId() == R.id.save_picture) {
            Log.e("save picture:", "1");
            Log.e("URI XL: ", URIXL);
            if (URIXL == null) return false;
            Log.e("save picture:", "2");
            File src = new File(URIXL.substring(7));
            String name = src.getName();
            String dstPath = Environment.getExternalStorageDirectory().getPath() + File.separatorChar +
                    Environment.DIRECTORY_DOWNLOADS + File.separatorChar + name;
            File dst = new File(dstPath);
            try {
                copy(src, dst);
                Toast.makeText(BigPicture.this, "Image saved. path: " + dstPath, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("some thing bad", "save image");
            }
        }
        return false;
    }

    public void copy(File src, File dst) throws IOException {
        Log.e("src: ", src.toString());
        Log.e("dst: ", dst.toString());
        OutputStream out = new FileOutputStream(dst);
        InputStream in = new FileInputStream(src);

        // Transfer bytes from in to out
        Log.e("in ", "copy");
        byte[] buf = new byte[10];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
