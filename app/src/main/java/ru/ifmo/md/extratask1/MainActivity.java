package ru.ifmo.md.extratask1;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<ArrayList<MyImage>> {
    private static final int IMAGES_LOADER_ID = 0;
    private static final String NO_INTERNET_MESSAGE = "Network error";
    private static final String DOWNLOAD_ERROR_MESSAGE = "Download error";
    ProgressDialog progressDialog;
    GridView gridView;
    MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        gridView = (GridView)findViewById(R.id.gridView);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadImagesService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
        getLoaderManager().initLoader(IMAGES_LOADER_ID, null, this);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(DownloadImagesService.TAG_PERCENT, -1);
            if (progress != -1) {
                progressDialog.setProgress(progress);
                progressDialog.setMessage("downloading... " + String.valueOf(progress) + "%");
                progressDialog.show();
                if (progress == 100) {
                    progressDialog.dismiss();
                    getLoaderManager().restartLoader(IMAGES_LOADER_ID, null, MainActivity.this);
                }
            } else {
                progressDialog.dismiss();
                getLoaderManager().restartLoader(IMAGES_LOADER_ID, null, MainActivity.this);
                showMessage(DOWNLOAD_ERROR_MESSAGE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            if (isOnline()) startService(new Intent(this, DownloadImagesService.class));
                else showMessage(NO_INTERNET_MESSAGE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MyImage>> listLoader, final ArrayList<MyImage> list) {
        gridView.setAdapter(new MyImagesListAdapter(this, list));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putInt(ImageViewActivity.APP_PREFERENCES_POSITION, position);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MyImage>> listLoader) {
        new MyImagesListLoader(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    public Loader<ArrayList<MyImage>> onCreateLoader(int i, Bundle bundle) {
        return new MyImagesListLoader(this);
    }

    private void showMessage(String str) {
        Toast tst = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        tst.setGravity(Gravity.TOP, 0, 0);
        tst.show();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable() && netInfo.isConnected();
    }
}
