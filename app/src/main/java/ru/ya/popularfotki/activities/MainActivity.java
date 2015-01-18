package ru.ya.popularfotki.activities;

import android.app.DownloadManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.TreeMap;

import ru.ya.popularfotki.OnePicture;
import ru.ya.popularfotki.R;
import ru.ya.popularfotki.UpdateIntentService;
import ru.ya.popularfotki.database.FotkiContentProvider;
import ru.ya.popularfotki.database.FotkiSQLiteHelper;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks < Cursor > {
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastFromDM;
    private DownloadManager dm;
    private OnePicture [] pictures;
    private int cur;
    private int cntTask;
    GridView gridView;
    SimpleCursorAdapter adapter;
    TreeMap < Long , Long > loadToPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadToPicture = new TreeMap<>();
        gridView = (GridView)findViewById(R.id.gridView);



        /// init something
        adapter = new SimpleCursorAdapter(this, R.layout.item, null, null, null, 1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View mView = (convertView == null)? inflater.inflate(R.layout.item, parent, false): convertView;
                ImageView imageView = (ImageView)mView.findViewById(R.id.imageView);
                Cursor cursor = getCursor();
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH)));
                imageView.setImageURI(uri);
                return mView;
            }
        };

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pictures = (OnePicture [])intent.getSerializableExtra(UpdateIntentService.ON_POST_EXECUTE);
                cur = 0;
                cntTask = 0;
                for (int i = 0; i < pictures.length; i++) {
                    OnePicture picture = pictures[i];
                    if (picture.getAlreadyLoad()) throw new Error();
                    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request( Uri.parse(picture.getURLS()));
                    cntTask++;
                    Long id = dm.enqueue(request);
                    loadToPicture.put(id, (long) i);
                }

            }
        };

        broadcastFromDM = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (reference == -1) throw new Error();
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Long id = loadToPicture.get(new Long(reference));
                        pictures[id.intValue()].setPath(uriString);
                        cur++;
                        if (cur == cntTask) {
                            ContentValues [] values = new ContentValues[cntTask];
                            for (int i = 0; i < cntTask; i++) {
                                values[i] = new ContentValues();
                                values[i].put(FotkiSQLiteHelper.COLUMN_URL_S, pictures[i].getURLS());
                                values[i].put(FotkiSQLiteHelper.COLUMN_URL_XL, pictures[i].getHttpXL());
                                values[i].put(FotkiSQLiteHelper.COLUMN_PATH, pictures[i].getPath());
                                values[i].put(FotkiSQLiteHelper.COLUMN_YANDEX_ID, pictures[i].getYandexId());
                            }
                            getContentResolver().bulkInsert(FotkiContentProvider.FOTKI_URI, values);
                        }
                    }
                }
            }
        };

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastFromDM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(UpdateIntentService.ON_POST_EXECUTE));
        registerReceiver(broadcastFromDM, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void update () {
        Intent intent = new Intent(this, UpdateIntentService.class);
        startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FotkiContentProvider.FOTKI_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_launcher) {
            Toast.makeText(this, "onOptionsItemSelected", Toast.LENGTH_LONG).show();
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
