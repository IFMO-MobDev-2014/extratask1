package freemahn.com.extratask1;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Freemahn on 16.01.2015.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<Entry>> {

    ArrayList<Entry> entries;
    Button btnRefresh;
    GridView gridView;
    ProgressBar progressBar;
    MyBroadcastReceiver myBroadcastReceiver;
    MyUpdateBroadcastReceiver myUpdateBroadcastReceiver;

    //FragmentPageAdapter mAdapter;
    ViewPager mPager;
    int mNumFragments = 0;
    int mNumItems = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        gridView = (GridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);


        Intent intentMyIntentService = new Intent(this, DownloadImagesService.class);
        startService(intentMyIntentService);


        myBroadcastReceiver = new MyBroadcastReceiver();
        myUpdateBroadcastReceiver = new MyUpdateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                DownloadImagesService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter(
                DownloadImagesService.ACTION_UPDATE);
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myUpdateBroadcastReceiver, intentFilter2);

        getLoaderManager().initLoader(0, null, this);

    }


    @Override
    public void onClick(View v) {
        Intent intentMyIntentService = new Intent(this, DownloadImagesService.class);
        startService(intentMyIntentService);

    }

    public Loader<ArrayList<Entry>> onCreateLoader(int i, Bundle bundle) {
        return new ImagesListLoader(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(myUpdateBroadcastReceiver);
    }
    @Override
    public void onLoadFinished(Loader<ArrayList<Entry>> listLoader, final ArrayList<Entry> list) {
        gridView.setAdapter(new ImageAdapter(this, list));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("link",list.get(position).linkBig);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Entry>> loader) {
        new ImagesListLoader(this);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setProgress(0);
            getLoaderManager().restartLoader(0, null, MainActivity.this);
        }
    }

    public class MyUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent
                    .getIntExtra(DownloadImagesService.TAG_PERCENT, 0);
          //  Log.d("PROGRESS", update + "");
            progressBar.setProgress(update);
        }
    }
}
