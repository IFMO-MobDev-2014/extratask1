package md.ifmo.ru.pictureoftheday;

import android.app.Activity;
import android.app.LoaderManager;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<ArrayList<YPicture>>{
    private static final int PICTURES_LOADER_ID = 0;
    ProgressBar progressBar;
    GridView gridView;
    MyBroadcastReceiver myBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(60);
        progressBar.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.gridView);

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(PicturesDownloader.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        getLoaderManager().initLoader(PICTURES_LOADER_ID, null, this);

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
        if (id == R.id.action_refresh) {
            if (isOnline()) {
                startService(new Intent(this, PicturesDownloader.class));
                progressBar.setVisibility(View.VISIBLE);
            }
            else showMessage("No Internet Connection");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    public Loader<ArrayList<YPicture>> onCreateLoader(int i, Bundle bundle) {
        return new YPicturesListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<YPicture>> listLoader, final ArrayList<YPicture> list) {
        gridView.setAdapter(new MyPicturesListAdapter(this, list));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PictureViewActivity.class);
                intent.putExtra("HR_LINK",list.get(position).hrLink);
                intent.putExtra("WEB_LINK",list.get(position).pageLink);
                intent.putExtra("TITLE",list.get(position).title);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<YPicture>> listLoader) {
        new YPicturesListLoader(this);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(PicturesDownloader.TAG_PERCENT, -1);
            if (progress != -1) {
                progressBar.setProgress(progress);
                if (progress == 60) {
                    progressBar.setVisibility(View.GONE);
                    getLoaderManager().restartLoader(PICTURES_LOADER_ID, null, MainActivity.this);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                getLoaderManager().restartLoader(PICTURES_LOADER_ID, null, MainActivity.this);
                showMessage("Download Error");
            }
        }

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable() && netInfo.isConnected();
    }

    private void showMessage(String str) {
        Toast tst = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        tst.setGravity(Gravity.BOTTOM, 0, 0);
        tst.show();
    }
}
