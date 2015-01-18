package ru.ya.popularfotki.activities;

import android.app.DownloadManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ru.ya.popularfotki.OnePicture;
import ru.ya.popularfotki.R;
import ru.ya.popularfotki.SquareImageView;
import ru.ya.popularfotki.UpdateIntentService;
import ru.ya.popularfotki.database.FotkiContentProvider;
import ru.ya.popularfotki.database.FotkiSQLiteHelper;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int PICTURE_PER_PAGE = 12;
    public static final String START = "START";

    private DownloadManager downloadManager;
    private ArrayList<OnePicture> pictures;
    private ProgressDialog progressDialog;
    GridView gridView;
    int pageNumber;
    int numberOfpage;
    int myProgress;
    TreeMap<Long, Long> loadToPicture;
    LoaderManager loaderManager;
    TextView viewNumber;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onBroadCast", "form intent");
            pictures = (ArrayList<OnePicture>) intent.getSerializableExtra(UpdateIntentService.ON_POST_EXECUTE);
            Log.e("list:", "size: " + pictures.size());
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Download images");
            progressDialog.setTitle("Title");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(pictures.size());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            myProgress = 0;
            for (int i = 0; i < pictures.size(); i++) {
                OnePicture picture = pictures.get(i);
                if (picture.getAlreadyLoad()) throw new Error();


                if (downloadManager == null)
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(picture.getURLS()));
                Long id = downloadManager.enqueue(request);

                loadToPicture.put(id, (long) i);
                //Log.e("add key val", id + " " + i);
            }

        }
    };


    private BroadcastReceiver broadcastFromDM = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.e("broadCast:", "after download");
            if (loadToPicture.isEmpty()) return;
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor c = downloadManager.query(query);
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == -1) throw new Error();
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    progressDialog.incrementProgressBy(1);
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    int id = loadToPicture.get(Long.valueOf(reference)).intValue();
                    loadToPicture.remove(Long.valueOf(reference));
                    //Log.e("delete broadcast Receiver key value", reference + " " + id);
                    pictures.get(id).setPath(uriString);
                    ContentValues values = new ContentValues();
                    values.put(FotkiSQLiteHelper.COLUMN_URL_S, pictures.get(id).getURLS());
                    values.put(FotkiSQLiteHelper.COLUMN_URL_XL, pictures.get(id).getHttpXL());
                    values.put(FotkiSQLiteHelper.COLUMN_PATH_S, pictures.get(id).getPath());
                    values.put(FotkiSQLiteHelper.COLUMN_YANDEX_ID, pictures.get(id).getYandexId());
                    getContentResolver().insert(FotkiContentProvider.FOTKI_URI, values);
                    //Log.e("after insert", "broadcast");
                    if (loadToPicture.isEmpty()) {
                        progressDialog.dismiss();
                        hardUpdate();
                    }
                }
            }
        }
    };

    SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(": ", "-------------------------------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewNumber = (TextView)findViewById(R.id.page_number);

        loadToPicture = new TreeMap<>();
        gridView = (GridView) findViewById(R.id.gridView);
        String [] from = {};
        int[] to = {};
        adapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View mView = (convertView == null) ? inflater.inflate(R.layout.item, parent, false) : convertView;
                SquareImageView imageView = (SquareImageView) mView.findViewById(R.id.picture);

                Cursor cursor = getCursor();
                cursor.moveToPosition(position);
                int id = cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH_S);
//                Log.e("adapter count cursru: ", cursor.getCount() + "");
                Uri uri = Uri.parse(cursor.getString(id));

                String yandexId = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_YANDEX_ID));

                imageView.setTag(yandexId);

                imageView.setImageURI(uri);
                return mView;
            }
        };
        loaderManager = getLoaderManager();
        pageNumber = 0;
        softUpdate();

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SquareImageView imageView = (SquareImageView)view.findViewById(R.id.picture);
                String yandexId = (String)imageView.getTag();
                Log.e("yandexId", yandexId);
                Intent intent = new Intent(MainActivity.this, BigPicture.class);
                intent.putExtra(FotkiSQLiteHelper.COLUMN_YANDEX_ID, yandexId);
                startActivity(intent);
            }

        });
        initButton();
    }

    void clearDownloadManager() {
       for (;!loadToPicture.isEmpty();) {

            Map.Entry< Long, Long > value = loadToPicture.firstEntry();
            Log.e("delete key values:", value.getKey() + " " + value.getValue());
            Long key = value.getKey();
            downloadManager.remove(key);
            loadToPicture.remove(key);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("on", "Pause");
        clearDownloadManager();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastFromDM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("on", "destroy");
        clearDownloadManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(UpdateIntentService.ON_POST_EXECUTE));
        registerReceiver(broadcastFromDM, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void hardUpdate() {
        pageNumber = 0;
        softUpdate();
    }

    private void softUpdate() {
        Log.e("soft Update", "!!!!!!!!!!!!!!!!!!! " + pageNumber);
        loaderManager.destroyLoader(0);
        String sortOrder = FotkiSQLiteHelper.COLUMN_ID + " " + FotkiSQLiteHelper.DESC;
        Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null, null, null, sortOrder);
        cursor.moveToFirst();
        Log.e("count in cursor: ", "" + cursor.getCount());
        numberOfpage = (cursor.getCount() + PICTURE_PER_PAGE - 1) / PICTURE_PER_PAGE;
        Bundle bundle = new Bundle();
        bundle.putInt(START, PICTURE_PER_PAGE * pageNumber);
        loaderManager.initLoader(0, bundle, this);
        //Log.e("update text: ")
        viewNumber.setText((pageNumber + 1) + " from " + numberOfpage);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Integer firstId = args.getInt(START);
        String sortOrder = FotkiSQLiteHelper.COLUMN_ID + " " + FotkiSQLiteHelper.DESC;
        //String selection = FotkiSQLiteHelper.COLUMN_ID + ">=? and " + FotkiSQLiteHelper.COLUMN_ID + "<?";
        //Log.e("left right: ", firstId + " " + (firstId + PICTURE_PER_PAGE));
        //String [] selectionArgs = {firstId.toString(), String.valueOf(firstId + PICTURE_PER_PAGE)};
        return new CursorLoader(this, FotkiContentProvider.FOTKI_URI, null, null, null, sortOrder +
                " LIMIT " + PICTURE_PER_PAGE + " OFFSET " + firstId );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) throw new Error();
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

        if (item.getItemId() == R.id.refresh_button) {
            Toast.makeText(this, "updating", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, UpdateIntentService.class);
            startService(intent);
            return true;
        }
        if (item.getItemId() == R.id.clear_button) {
            getContentResolver().delete(FotkiContentProvider.FOTKI_URI, FotkiSQLiteHelper.COLUMN_ID + ">0", null);
        }
        return super.onOptionsItemSelected(item);
    }

    void initButton() {
        Log.e("********************************", "");
        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNumber > 0)
                    pageNumber--;
                softUpdate();
            }
        });
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("next", "onClick " + pageNumber + " " + numberOfpage);
                if (pageNumber + 1 < numberOfpage)
                    pageNumber++;
                softUpdate();
            }
        });
    }

}
