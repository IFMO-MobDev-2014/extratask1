package com.example.maksim.photoview;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks <List <Image>> {

    MyBroadcastReceiver receiver = null;
    ProgressDialog dialog;
    ImageAdapter adapter;
    int currentPosition = 0;
    List <Image> allImages;
    GridView view;
    float startX = 0;
    float finalX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (GridView) findViewById(R.id.gridView);
        currentPosition = getIntent().getIntExtra("current", 0);
        adapter = new ImageAdapter(this, new ArrayList<Image>());
        dialog = new ProgressDialog(this);
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("RESPONSE");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
        adapter = new ImageAdapter(this, new ArrayList<Image>());
        getLoaderManager().initLoader(1, null, MainActivity.this);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullScreenImage.class);
                intent.putExtra("position", currentPosition + position);
                intent.putExtra("current", currentPosition);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void onUpdate(View view) {
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("RESPONSE");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
        Intent intent = new Intent(this, ImageDownloader.class);
        startService(intent);
    }

    public void onNext(View view) {
        currentPosition += 10;
        if (currentPosition >= allImages.size()) {
            currentPosition = 0;
        }
        adapter.setImages(allImages.subList(currentPosition, Math.min(currentPosition + 10, allImages.size())));
        adapter.notifyDataSetChanged();
    }

    public void onPrev(View view) {
        currentPosition -= 10;
        if (currentPosition < 0) {
            currentPosition = allImages.size() - 10;
        }
        adapter.setImages(allImages.subList(currentPosition, currentPosition + 10));
        adapter.notifyDataSetChanged();
    }

    @Override
    public Loader <List <Image>> onCreateLoader(int i, Bundle bundle) {
        return new ImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader <List <Image>> listLoader, List <Image> images) {
        unregisterReceiver(receiver);
        allImages = images;
        if (allImages.size() > 0) {
            adapter.setImages(allImages.subList(currentPosition, currentPosition + 10));
            adapter.notifyDataSetChanged();
        } else {
            onUpdate(view);
        }
    }

    @Override
    public void onLoaderReset(Loader <List <Image>> listLoader) {
        adapter = new ImageAdapter(this, new ArrayList<Image>());
        adapter.notifyDataSetChanged();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("percent", -1);
            if (progress != -1) {
                dialog.setProgress(progress);
                dialog.setMessage(Integer.toString(progress) + " % completed");
                dialog.show();
                if (progress == 100) {
                    dialog.dismiss();
                    getLoaderManager().restartLoader(1, null, MainActivity.this);
                }
            } else {
                dialog.setMessage("Downloading error");
                dialog.show();
            }
        }
    }
}
