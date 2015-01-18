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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
    private GestureDetector gestureDetector;

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
        gestureDetector = initGestureDetector();

        View view = findViewById(R.id.grid_view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

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
                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.EXTRA_ROW_ID, rowId);
                startActivity(intent);
            }
        });
        Log.d("Tag", "count = " + getNumberOfRows());
        showNextXPhotos(10);
    }

    private void showNextXPhotos(int number) {
        int startIndex = getLastIndex(getApplicationContext());
        int numRows = getNumberOfRows();
        if (startIndex >= numRows) {
            //Recycle Images
            startIndex = 1;
        }
        int lastIndex = startIndex + number;
        if (lastIndex > numRows) {
            startIndex = Math.max(numRows - number, 1);
            lastIndex = Math.min(startIndex + number, numRows);
        }
        setLastIndex(getApplicationContext(), lastIndex);
        Bundle args = new Bundle();
        args.putInt("start_index", startIndex);
        args.putInt("last_index", lastIndex);
        getLoaderManager().restartLoader(0, args, this);
    }

    private void showPreviousXPhotos(int number) {
        int lastIndex = getLastIndex(getApplicationContext());
        int numRows = getNumberOfRows();
        int startIndex = lastIndex - number;
        if (lastIndex <= 0) {
            startIndex = 1;
            lastIndex = Math.min(startIndex + number, numRows);
        }
        setLastIndex(getApplicationContext(), startIndex);
        Bundle args = new Bundle();
        args.putInt("start_index", startIndex);
        args.putInt("last_index", lastIndex);
        getLoaderManager().restartLoader(0, args, this);
    }

    private GestureDetector initGestureDetector() {
        return new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            private MySwipeDetector detector = new MySwipeDetector();
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    if (detector.isSwipeLeft(e1, e2, velocityX)) {
                        Log.d("Tag", "swipe LEFT");
                        showNextXPhotos(10);
                    } else if (detector.isSwipeRight(e1, e2, velocityX)) {
                        Log.d("Tag", "swipe RIGHT");
                        showPreviousXPhotos(10);
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNextXPhotos(10);
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
        int startIndex = args.getInt("start_index");
        int lastIndex = args.getInt("last_index");
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
