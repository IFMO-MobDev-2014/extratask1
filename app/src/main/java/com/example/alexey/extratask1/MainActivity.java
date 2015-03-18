package com.example.alexey.extratask1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;


public class MainActivity extends Activity implements AppReceiver.Receiver {
    public static int height;
    public static int width;
    static AppReceiver mReceiver;
    public String name;
    ProgressBar mProgress;
    GridView lvMain;
    Cursor cursor;
    BoxAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        height = getWindowManager().getDefaultDisplay().getHeight();
        width = getWindowManager().getDefaultDisplay().getWidth();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fact);
        cursor = getContentResolver().query(provider.CONTENT_URI, null, null, null, null);
        boxAdapter = new BoxAdapter(this, R.layout.lel, cursor, new String[]{provider.DATE}, new int[]{R.id.imageView});
        lvMain = (GridView) findViewById(R.id.gridView);
        lvMain.setAdapter(boxAdapter);
        lvMain.setNumColumns(3);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra("img", i);
                startActivity(intent);
            }
        });
        if (cursor.getCount() == 0) {
            go();
        }
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.INVISIBLE);
        mReceiver = new AppReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    public void go() {
        getContentResolver().delete(provider.CONTENT_URI, null, null);
        final Intent intent = new Intent("SOME_COMMAND_ACTION", null, MainActivity.this, IServise.class);
        mReceiver = new AppReceiver(new Handler());
        mReceiver.setReceiver(this);
        intent.putExtra("1", mReceiver);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            go();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        Log.i("Result", "got");
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        switch (resultCode) {
            case 2:
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setProgress(0);
                break;
            case 6:
                mProgress.setProgress(data.getInt("p"));
                break;
            case 7:
                mProgress.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
