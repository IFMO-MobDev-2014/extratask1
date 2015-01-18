package year2013.ifmo.imageloader;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AsyncResponce{

    private ImageView _imageView;
    private ArrayList<CustomImage> _links;
    Bitmap bmp;
    private GridView gridview;
    private ImageAdapter adapter;
    private ProgressBar progressBar;
    //String url = "http://cs621219.vk.me/v621219527/a402/7tJ0rxruG7A.jpg";
    private ImageDBHelper _dbHelper;
    private SQLiteDatabase _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _links = new ArrayList<CustomImage>();

        _dbHelper = new ImageDBHelper(this);
        _db = _dbHelper.getWritableDatabase();

        _links = _dbHelper.GetImages(_db);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.INVISIBLE);

        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new ImageAdapter(this, _links);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //test version
                Intent i = new Intent(Intent.ACTION_VIEW);
                CustomImage ci = (CustomImage)adapter.getItem(position);
                i.setData(Uri.parse(ci.BigImageLink));
                startActivity(i);
            }
        });

    }

    public void photosOfTheDay_click(View view) {
        adapter.clearList();
        progressBar.setVisibility(View.VISIBLE);
        GetPhotosFromYandex q = new GetPhotosFromYandex();
        q.SetProgressBar(progressBar);
        q.delegate = this;
        AsyncTask<Void, Integer, List<CustomImage>> res = q.execute();
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
    public void ProcessFinished(Object obj) {
        _dbHelper.ClearImagesTable(_db);
        adapter.addLinks((List<CustomImage>)obj);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
        _dbHelper.SaveImages(_db, (List<CustomImage>)obj);
    }
}