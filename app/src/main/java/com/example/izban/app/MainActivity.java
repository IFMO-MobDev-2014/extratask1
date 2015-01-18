package com.example.izban.app;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    private ProgressBar progressBar;
    MyResultReceiver resultReceiver;

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
        progressBar.setVisibility(View.INVISIBLE);
        resultReceiver = new MyResultReceiver(new Handler());
        resultReceiver.setReceiver(this);
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
            Toast.makeText(this, "refreshing", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, DownloadAllImagesService.class).putExtra(Constants.RECEIVER, resultReceiver));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case Constants.RECEIVER_STARTED:
                progressBar.setMax(DownloadAllImagesService.PICTURES);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case Constants.RECEIVER_RUNNING:
                progressBar.setProgress(progressBar.getProgress() + 1);
                break;
            case Constants.RECEIVER_FINISHED:
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "refreshed", Toast.LENGTH_SHORT).show();
                break;
            case Constants.RECEIVER_FAILED:
                Toast.makeText(this, "failed to refresh", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
