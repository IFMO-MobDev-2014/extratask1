package ru.ya.popularfotki.activities;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import ru.ya.popularfotki.R;
import ru.ya.popularfotki.database.FotkiContentProvider;
import ru.ya.popularfotki.database.FotkiSQLiteHelper;

public class BigPicture extends Activity {
    DownloadManager dm;
    Long downloadId;
    String yandexId;
    ImageView imageView;
    BroadcastReceiver DMBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor c = dm.query(query);
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == downloadId && c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Log.e("uriStirng:", uriString);
                    ContentValues values = new ContentValues();
                    values.put(FotkiSQLiteHelper.COLUMN_PATH_XL, uriString);
                    getContentResolver().update(FotkiContentProvider.FOTKI_URI, values,
                            FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?", new String[]{yandexId});
                    imageView.setImageURI(Uri.parse(uriString));
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_big_picture);
        //getActionBar().hide();

        Intent intent = getIntent();
        yandexId = intent.getStringExtra(FotkiSQLiteHelper.COLUMN_YANDEX_ID);
        String[] selectionArgs = {yandexId};
        Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null,
                FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?", selectionArgs, null);
        if (cursor == null || cursor.getCount() != 1) throw new Error();
        cursor.moveToFirst();
        String pathXL = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH_XL));
        String URLXL = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_URL_XL));
        imageView = (ImageView) findViewById(R.id.big_image);
        if (pathXL == null) {
            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadId = dm.enqueue(new DownloadManager.Request(Uri.parse(URLXL)));
        } else {
            imageView.setImageURI(Uri.parse(pathXL));
        }
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
}
