package ru.ifmo.md.extratask1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    ArrayList<Bitmap> bitmaps;
    BitmapAdapter adapter;
    Button upd;

    private ResponseReceiver responseReceiver;
    private IntentFilter mStatusIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upd = (Button) findViewById(R.id.update);

        mStatusIntentFilter = new IntentFilter(
                ImageIntentService.BROADCAST_ACTION);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(responseReceiver, mStatusIntentFilter);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        bitmaps = new ArrayList<Bitmap>();
        adapter = new BitmapAdapter(this, bitmaps);
        gridView.setAdapter(adapter);

        startLoading();
    }

    private void startLoading() {
        Intent intent = new Intent(this, ImageIntentService.class);
        startService(intent);
    }

    public class ImageLoader extends AsyncTask<String, Void, ArrayList<Bitmap>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... value) {
            ArrayList<Bitmap> images = new ArrayList<Bitmap>();
            Cursor cursor = getContentResolver().query(Image.JustImage.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    byte[] bytes = cursor.getBlob(cursor.getColumnIndex(Image.JustImage.SMALL_NAME));
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                    theImage = Bitmap.createScaledBitmap(theImage, 200, 200, true);
                    images.add(theImage);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> answer) {
            for (Bitmap bitmap : answer) {
                bitmaps.add(bitmap);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {}

        public void onReceive(Context context, Intent intent) {
            int persents = intent.getIntExtra(ImageIntentService.EXTRA_PERCENTS, -1);
            upd.setText("" + persents);
            if (persents == 100) {
                upd.setText(getString(R.string.update_button));
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.execute();
            }
        }
    }

    public void updateAll(View view) {
    }

    public void prevPage(View view) {
    }

    public void nextPage(View view) {
    }
}
