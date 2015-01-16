package ru.ifmo.md.flickrclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class ViewActivity extends ActionBarActivity {

    public static final String IMAGE_ID = "IMAGE_ID";

    private long view_id;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        view_id = getIntent().getLongExtra(IMAGE_ID, -1);
        imageView = (ImageView) findViewById(R.id.full_image);
        if (view_id != -1) {
            ImageTask imageTask = new ImageTask(imageView, getContentResolver());
            imageTask.execute(view_id);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
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

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ACTIVITY", "broadcast received");
            Cursor cursor = getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, null, null, null);
            Log.d("ACTIVITY", "broadcast " + cursor.getCount());
        }
    }
}
