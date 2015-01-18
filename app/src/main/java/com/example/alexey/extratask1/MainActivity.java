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
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class MainActivity extends Activity implements AppReceiver.Receiver {
    ProgressBar mProgress;
    static AppReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fact);
       // Button button = (Button) findViewById(R.id.button);
     //   button.setVisibility(View.INVISIBLE);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.INVISIBLE);
        final Intent intent = new Intent("SOME_COMMAND_ACTION", null, MainActivity.this, IServise.class);
       // intent.putExtra("word", ((EditText) findViewById(R.id.editText)).getText().toString());
        mReceiver = new AppReceiver(new Handler());
        mReceiver.setReceiver(this);
        intent.putExtra("1", mReceiver);
        //Button button = (Button) findViewById(R.id.button);
       // button.setVisibility(View.INVISIBLE);
        startService(intent);
    }

   public void go(View view) {
       final Intent intent = new Intent("SOME_COMMAND_ACTION", null, MainActivity.this, IServise.class);
      // intent.putExtra("word", ((EditText) findViewById(R.id.editText)).getText().toString());
       mReceiver = new AppReceiver(new Handler());
       mReceiver.setReceiver(this);
       intent.putExtra("1", mReceiver);
      // /Button button = (Button) findViewById(R.id.button);
       //button.setVisibility(View.INVISIBLE);
       startService(intent);
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
           //     Button button = (Button) findViewById(R.id.button);
                ArrayList<Bitmap> f=  data.getParcelableArrayList("pic");
               // button.setVisibility(View.VISIBLE);
                BoxAdapter  boxAdapter = new BoxAdapter(this, f);

                // настраиваем список
                GridView lvMain = (GridView) findViewById(R.id.gridView);
                lvMain.setAdapter(boxAdapter);
                lvMain.setNumColumns(3);
             //   gvMain.setAdapter(boxAdapter);
                break;
        }
    }
}
