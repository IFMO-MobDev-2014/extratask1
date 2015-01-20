package ru.ya.fotki.activities;

import android.app.DownloadManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ru.ya.fotki.OnePicture;
import ru.ya.fotki.R;
import ru.ya.fotki.SquareImageView;
import ru.ya.fotki.UpdateIntentService;
import ru.ya.fotki.database.FotkiContentProvider;
import ru.ya.fotki.database.FotkiSQLiteHelper;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int PICTURE_PER_PAGE = 10;
    public static final String START = "START";
    public static final String COUNT_PICTURE_FOR_DOWNLOAD = "PICTURE_FOR_DOWNLOAD";

    private DownloadManager downloadManager;
    private ArrayList<OnePicture> pictures;
    private ProgressDialog progressDialog;
    private ContentResolver contentResolver;
    GridView gridView;
    int pageNumber;
    int numberOfPage;
    TreeMap<Long, Long> loadToPicture;
    LoaderManager loaderManager;
    TextView viewNumber;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onBroadCast", "form intent");
            pictures = (ArrayList<OnePicture>) intent.getSerializableExtra(UpdateIntentService.ON_POST_EXECUTE);
            Log.e("list:", "size: " + pictures.size());
            if (progressDialog != null) progressDialog.cancel();

            if (pictures.size() > 0) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Download images");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(pictures.size());
                progressDialog.show();
            }
            for (int i = 0; i < pictures.size(); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                OnePicture picture = pictures.get(i);
                if (picture.getAlreadyLoad()) throw new Error();
                if (downloadManager == null)
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(picture.getURLS()));
                //request.setDestinationInExternalFilesDir(MainActivity.this,
                        //Environment.getExternalStorageDirectory().getPath() + "/Fotki",  "/small/picture.png");
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
                    pictures.get(id).setPath(uriString);
                    ContentValues values = new ContentValues();
                    values.put(FotkiSQLiteHelper.COLUMN_URL_S, pictures.get(id).getURLS());
                    values.put(FotkiSQLiteHelper.COLUMN_URL_XL, pictures.get(id).getHttpXL());
                    values.put(FotkiSQLiteHelper.COLUMN_PATH_S, pictures.get(id).getPath());
                    values.put(FotkiSQLiteHelper.COLUMN_YANDEX_ID, pictures.get(id).getYandexId());
                    getContentResolver().insert(FotkiContentProvider.FOTKI_URI, values);
                    if (loadToPicture.isEmpty()) {
                        pageNumber = (getCountPictures() + PICTURE_PER_PAGE - 1) / PICTURE_PER_PAGE - 1;
                        //Log.e("pageNumber: ", pageNumber + "");
                        progressDialog.dismiss();
                        softUpdate();
                    }
                }
            }
        }
    };

    SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver = getContentResolver();
        viewNumber = (TextView) findViewById(R.id.page_number);
        loadToPicture = new TreeMap<>();
        gridView = (GridView) findViewById(R.id.gridView);
        String[] from = {};
        int[] to = {};

        adapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View mView = (convertView == null) ? inflater.inflate(R.layout.item, parent, false) : convertView;
//                View mView = super.getView(position, convertView, parent);
                SquareImageView imageView = (SquareImageView) mView.findViewById(R.id.picture);

                Cursor cursor = getCursor();
                Log.e("count position:", cursor.getCount() + " " + position);
                Log.e("is closed: ", cursor.isClosed() + "");
                cursor.moveToPosition(position);
                int id = cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_PATH_S);
                Uri uri = Uri.parse(cursor.getString(id));
                String yandexId = cursor.getString(cursor.getColumnIndex(FotkiSQLiteHelper.COLUMN_YANDEX_ID));
                imageView.setTag(yandexId);
                imageView.setImageURI(uri);
                return mView;
            }
        };
        loaderManager = getLoaderManager();

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SquareImageView imageView = (SquareImageView) view.findViewById(R.id.picture);
                String yandexId = (String) imageView.getTag();
                Log.e("yandexId", yandexId);
                Intent intent = new Intent(MainActivity.this, BigPicture.class);
                intent.putExtra(FotkiSQLiteHelper.COLUMN_YANDEX_ID, yandexId);
                startActivity(intent);
            }

        });
        initButton();
        if (getCountPictures() < PICTURE_PER_PAGE) {
            updateLastPage();
        }
        else {
            pageNumber = 0;
            softUpdate();
        }
    }

    void clearDownloadManager() {
        for (; !loadToPicture.isEmpty(); ) {
            Map.Entry<Long, Long> value = loadToPicture.firstEntry();
            Long key = value.getKey();
            downloadManager.remove(key);
            loadToPicture.remove(key);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    public int getCountPictures() {
        return contentResolver.query(FotkiContentProvider.FOTKI_URI, null, null, null, null).getCount();
    }

    private void softUpdate() {
//        Log.e("soft Update", "!!!!!!!!!!!!!!!!!!! " + pageNumber);
        loaderManager.destroyLoader(0);

        String sortOrder = FotkiSQLiteHelper.COLUMN_ID + " " + FotkiSQLiteHelper.DESC;
        Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null, null, null, sortOrder);
        cursor.moveToFirst();
        numberOfPage = (cursor.getCount() + PICTURE_PER_PAGE - 1) / PICTURE_PER_PAGE;
        Bundle bundle = new Bundle();
        bundle.putInt(START, PICTURE_PER_PAGE * pageNumber);
        loaderManager.initLoader(0, bundle, this);
        //Log.e("update text: ")

        viewNumber.setText((pageNumber + 1) + " from " + numberOfPage);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Integer firstId = args.getInt(START);
        String sortOrder = FotkiSQLiteHelper.COLUMN_ID + " " + FotkiSQLiteHelper.ASC;
        return new CursorLoader(this, FotkiContentProvider.FOTKI_URI, null, null, null, sortOrder + " LIMIT " + PICTURE_PER_PAGE + " OFFSET " + firstId);
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

    void updateLastPage() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Download images");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Intent intent = new Intent(this, UpdateIntentService.class);
        int cntPictures = getCountPictures();
        //getResources().getConfiguration().getLayoutDirection();
        int cntDownload = PICTURE_PER_PAGE - (cntPictures % PICTURE_PER_PAGE);
        intent.putExtra(COUNT_PICTURE_FOR_DOWNLOAD, cntDownload);
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_button) {
            updateLastPage();
            return true;
        }
        if (item.getItemId() == R.id.clear_button) {
            getContentResolver().delete(FotkiContentProvider.FOTKI_URI, FotkiSQLiteHelper.COLUMN_ID + ">0", null);
        }
        return super.onOptionsItemSelected(item);
    }

    void initButton() {
        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNumber > 0) {
                    pageNumber--;
                    softUpdate();
                }
            }
        });
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("next", "onClick " + pageNumber + " " + numberOfPage);
                if (pageNumber + 1 < numberOfPage) {
                    pageNumber++;
                    softUpdate();
                } else {
                    updateLastPage();
                }
            }
        });
    }

}
