package ru.ifmo.md.extratask1.photoclient;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import ru.ifmo.md.extratask1.photoclient.database.ImagesProvider;
import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView gridView;
    private ImageAdapter mAdapter;
    private Cursor mCursor;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkAvailable()) {
            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        ImagesLoader.startActionLoadFeed(getApplicationContext());

        gridView = (GridView) findViewById(R.id.grid_view);
        mAdapter = new ImageAdapter(this, null, false);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int rowId = cursor.getInt(cursor.getColumnIndex(ImagesTable.COLUMN_ID));
                cursor.close();
                Intent intent = new Intent(getApplicationContext(), FullImageActivity.class);
                intent.putExtra(FullImageActivity.EXTRA_ROW_ID, rowId);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this);

        Cursor cursor = getContentResolver().query(ImagesProvider.CONTENT_URI,
                new String[] {ImagesTable.COLUMN_ID},
                null, null, null);
        Log.d("Tag", "count = " + cursor.getCount());
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ImagesLoader.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
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
        if (id == R.id.action_update) {
            Log.d("Tag", "Updating start");
            return true;
        } else {
            Log.d("Tag", "Next page");
            getLoaderManager().restartLoader(0, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    private static int getLastIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("shared_last_index", 1);
    }

    private static void setLastIndex(Context context, int lastShownIndex) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt("shared_last_index", lastShownIndex)
                .commit();
    }

    private int getNumberOfRows() {
        Cursor cursor = getContentResolver().query(
                ImagesProvider.CONTENT_URI,
                new String[] { ImagesTable.COLUMN_ID },
                null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int startIndex = getLastIndex(getApplicationContext());
        int numRows = getNumberOfRows();
        if (startIndex >= numRows) {
            //Recycle Images
            startIndex = 1;
        }
        int lastIndex = startIndex + 10;
        if (lastIndex > numRows) {
            startIndex = Math.max(numRows - 10, 1);
            lastIndex = Math.min(startIndex + 10, numRows);
        }
        setLastIndex(getApplicationContext(), lastIndex);
        final String whereClause = ImagesTable.COLUMN_ID + " >= " + startIndex + " AND " +
                                   ImagesTable.COLUMN_ID + " < " + lastIndex;
        return new CursorLoader(this, ImagesProvider.CONTENT_URI, null, whereClause, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mAdapter.swapCursor(mCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
