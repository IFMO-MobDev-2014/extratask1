package ru.ifmo.md.flickrclient;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView gridView = null;
    private GridAdapter gridAdapter = null;
    private MyBroadcastReceiver myBroadcastReceiver;
    private Intent fullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullView = new Intent(this, ViewActivity.class);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fullView.putExtra(ViewActivity.IMAGE_ID, id);
                startActivity(fullView);
            }
        });

        getLoaderManager().initLoader(1, null, this);

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(UrlsDownloadService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FlickrCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            Intent i = new Intent(this, UrlsDownloadService.class);
            i.putExtra(UrlsDownloadService.DOWNLOAD_ID, -1);
            startService(i);
        } else {
            gridAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gridAdapter.changeCursor(null);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ACTIVITY", "broadcast received");
            Cursor cursor = getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, null, null, null);
            gridAdapter.changeCursor(cursor);
            Log.d("ACTIVITY", "broadcast " + cursor.getCount());
        }
    }
}
