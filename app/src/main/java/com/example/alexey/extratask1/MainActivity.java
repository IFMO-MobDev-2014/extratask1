package com.example.alexey.extratask1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class MainActivity extends Activity implements AppReceiver.Receiver {
    ProgressBar mProgress;
    static AppReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fact);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.INVISIBLE);
        final Intent intent = new Intent("SOME_COMMAND_ACTION", null, MainActivity.this, IServise.class);
        mReceiver = new AppReceiver(new Handler());
        mReceiver.setReceiver(this);
        intent.putExtra("1", mReceiver);
        startService(intent);
    }

   public void go(View view) {
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

        if (id == R.id.action_settings) {
            return true;
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
                break;
            case 5:
                mProgress.setVisibility(View.INVISIBLE);
                ArrayList<Bitmap> f=  data.getParcelableArrayList("pic");
                BoxAdapter  boxAdapter = new BoxAdapter(this, f);

                GridView lvMain = (GridView) findViewById(R.id.gridView);
                lvMain.setAdapter(boxAdapter);
                lvMain.setNumColumns(3);
                lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ImageView view1=(ImageView)view;

                    }
                });
                break;
        }
    }
}
