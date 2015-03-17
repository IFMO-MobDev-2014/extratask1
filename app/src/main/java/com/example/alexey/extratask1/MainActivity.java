package com.example.alexey.extratask1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class MainActivity extends Activity implements AppReceiver.Receiver {
    ProgressBar mProgress;
    static AppReceiver mReceiver;
    public  String name;
    public int height;
    public static int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        height = getWindowManager().getDefaultDisplay().getHeight();
        width = getWindowManager().getDefaultDisplay().getWidth();
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
    AlertDialog alertPB;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_example) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alertPB=alert.create();
            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setVisibility(View.VISIBLE);
            alertPB.setView(progressBar);
            alert.setTitle("Deete");
            final EditText editText = new EditText(MainActivity.this);
            editText.setSingleLine(true);
            alert.setView(editText);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    startService((new Intent(MainActivity.this, IServise.class)).putExtra("name",editText.getText().toString()).putExtra("1", mReceiver));
                    alertPB.show();
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    GridView lvMain;
    ArrayList<Bitmap> f= new ArrayList<Bitmap>();
    BoxAdapter boxAdapter;
    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        Log.i("Result", "got");
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        switch (resultCode) {
            case 2:
                f= new ArrayList<Bitmap>();

                boxAdapter = new BoxAdapter(this, f);
                lvMain = (GridView) findViewById(R.id.gridView);
                lvMain.setAdapter(boxAdapter);
                lvMain.setNumColumns(3);
                lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // ImageView view1=(ImageView)view;
                        // Intent intent = new Intent(MainActivity.this, ShowResalts.class);
                        //   Bundle bundle = new Bundle();


                    }
                });

                mProgress.setVisibility(View.VISIBLE);
                break;
            case 5:
                mProgress.setVisibility(View.INVISIBLE);
                try {
                    alertPB.cancel();
                } catch (Exception e){

                }/*
                */
                Bitmap bitmap = Bitmap.createBitmap(data.getInt("wth"),data.getInt("hth"), Bitmap.Config.ARGB_8888);
                bitmap.setPixels(data.getIntArray("pic"), 0, data.getInt("wth"), 0, 0, data.getInt("wth"), data.getInt("hth"));
                f.add(bitmap);
               // boxAdapter = new BoxAdapter(this, f);

                break;
            case 6:
                Bitmap bitmap1 = Bitmap.createBitmap(data.getInt("wth"), data.getInt("hth"), Bitmap.Config.ARGB_8888);
                bitmap1.setPixels(data.getIntArray("pic"), 0, data.getInt("wth"), 0, 0, data.getInt("wth"), data.getInt("hth"));
                f.add(bitmap1);
             //   boxAdapter = new BoxAdapter(this, f);
                break;
        }
    }
}
