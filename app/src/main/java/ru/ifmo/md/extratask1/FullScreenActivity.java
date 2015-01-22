package ru.ifmo.md.extratask1;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;


public class FullScreenActivity extends ActionBarActivity {
    public static final String EXTRA_LARGE = "extra_large";
    public static final String EXTRA_ORIG = "extra_orig";
    public static final String EXTRA_ID = "extra_id";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        String largeUrl = getIntent().getStringExtra(EXTRA_LARGE);
        String origUrl = getIntent().getStringExtra(EXTRA_ORIG);
        long id = getIntent().getLongExtra(EXTRA_ID, 1);

        IntentFilter mStatusIntentFilter = new IntentFilter(
                ImageIntentService.BROADCAST_LARGE);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(responseReceiver, mStatusIntentFilter);

        Uri uri = ContentUris.withAppendedId(Tables.Images.CONTENT_URI, id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(Tables.Images.LARGE_NAME));
        if (bytes != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(theImage);
        } else {
            Intent intent = new Intent(this, ImageIntentService.class);
            intent.setAction(ImageIntentService.ACTION_LARGE);
            intent.putExtra(ImageIntentService.EXTRA_LARGE, largeUrl);
            intent.putExtra(ImageIntentService.EXTRA_ID, id);
            startService(intent);
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {}

        public void onReceive(Context context, Intent intent) {
            Uri uri = ContentUris.withAppendedId(Tables.Images.CONTENT_URI,
                    getIntent().getLongExtra(EXTRA_ID, 1));
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(Tables.Images.LARGE_NAME));
            if (bytes != null) {
                ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(theImage);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
