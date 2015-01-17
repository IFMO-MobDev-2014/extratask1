package ru.ifmo.md.flickrclient;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView gridView = null;
    private GridAdapter gridAdapter = null;
    private Intent fullView;
    private ProgressDialog progressBar;

    public static final String sortOrder = DBFlickr.ID1 + " ASC " + " LIMIT 10";


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

    }

    private void downloadAll() {
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(false);
        progressBar.setMessage("Downloading images...");
        progressBar.setMax(UrlsDownloadService.COUNT_IMAGES);
        progressBar.setCancelable(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();
        Intent i = new Intent(this, UrlsDownloadService.class);
        i.putExtra(UrlsDownloadService.RESULT_RECEIVER, new ProgressReceiver(new Handler()));
        startService(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_settings:
                return true;
            case R.id.refreshButton:
                downloadAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FlickrCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            downloadAll();
        } else {
            gridAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gridAdapter.changeCursor(null);
    }

    public void loadMore(View view) {
        Cursor cursor = gridAdapter.getCursor();
        cursor.moveToLast();
        long row_id = cursor.getLong(cursor.getColumnIndexOrThrow(DBFlickr.ID1));
        Log.d("MAIN_ACTIVITY", String.valueOf(row_id));
        cursor = getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, DBFlickr.ID1 + " > ?",
                new String[] {String.valueOf(row_id)}, MainActivity.sortOrder);
        if (cursor.getCount() == 0) {
            Toast toast = Toast.makeText(this, "Start from begging", Toast.LENGTH_SHORT);
            toast.show();
            cursor = getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, null, null, MainActivity.sortOrder);
        }
        gridAdapter.swapCursor(cursor);
    }


    public class ProgressReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public ProgressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                int progress = resultData.getInt(UrlsDownloadService.PROGRESS);
                Log.d("Receiver", String.valueOf(progress));

                if (progress == -1) {
                    progressBar.cancel();
                    Toast toast = Toast.makeText(getApplicationContext(), "Error on updating", Toast.LENGTH_SHORT);
                    toast.show();
                }else if (progress == UrlsDownloadService.COUNT_IMAGES) {
                    progressBar.cancel();
                    Cursor cursor = getContentResolver().query(FlickrContentProvider.PHOTO_URI, null, null, null,
                            MainActivity.sortOrder);
                    gridAdapter.changeCursor(cursor);
                } else {
                    progressBar.setProgress(progress);
                }
            }
        }
    }
}
