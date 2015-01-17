package com.example.izban.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
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

        if (id == R.id.action_refresh) {
            Toast.makeText(this, "trying to refresh", Toast.LENGTH_SHORT).show();
            //new Thread(myThread).start();
            startService(new Intent(this, DownloadService.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /*Runnable myThread = new Runnable() {

        @Override
        public void run() {
            progressBar.setMax(10);
            progressBar.setProgress(0);
            while (progressBar.getProgress() < progressBar.getMax()) {

                try {
                    Thread.sleep(1000);
                    myHandle.sendMessage(myHandle.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            progressBar.setMax(0);
        }

        Handler myHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setProgress(progressBar.getProgress() + 1);
            }
        };
    };*/
}
