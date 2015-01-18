package ru.ifmo.md.photooftheday;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.ifmo.md.photooftheday.photosdownloader.PhotosDownloadTask;
import ru.ifmo.md.photooftheday.photosdownloader.PhotosParams;


public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";

    private static final int columnsCounter = 2;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* !!!testing!!! */
        /* !!!end of testing!!! */
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnsCounter));
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, columnsCounter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(new ArrayList<Bitmap>());
        recyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.add(0, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        try {
            List<Bitmap> list = new PhotosDownloadTask().execute(new PhotosParams.Builder().setCounter(10).setImageSize(1).build()).get();
            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    recyclerAdapter.add(i + 1, list.get(i));
                }
            } else {
                Log.d(TAG, "Failed download");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
}
