package ru.ifmo.ctddev.filippov.extratask1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String API_KEY = "6ff27bc7aa54ac32d9b42e90c83c50cb";
    public static final String API_SECRET_KEY = "cfd26d2770b06e82";
    private ProgressBar progressBar;
    private TextView photoStream;
    private ViewFlipper viewFlipper;
    private Intent intent;
    private GridView gridView;
    private PhotoAdapter photoAdapter;
    private Handler handler;
    private int photosShown = 0;
    private int currentPage = 1;
    private boolean updating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBarHorizontal);
        photoStream = (TextView) findViewById(R.id.photostream);
        photoStream.setText(R.string.title);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        intent = new Intent(this, ImageActivity.class);
        gridView = (GridView) findViewById(R.id.gridView1);
        List<MyPhoto> photos = new ArrayList<MyPhoto>();
        photoAdapter = new PhotoAdapter(photos);
        gridView.setAdapter(photoAdapter);
        gridView.setOnItemClickListener(listener);
        getLoaderManager().initLoader(1, null, this);
        loadAllPhotos();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        if (photosShown == 0) {
                            updating = true;
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        photosShown++;

                        if (photosShown == MyIntentService.photosOnPage) {
                            progressBar.setVisibility(View.INVISIBLE);
                            updating = false;
                        }
                        progressBar.setProgress(photosShown);
                        break;
                    case 1:
                        progressBar.setVisibility(View.INVISIBLE);
                        updating = false;
                }
                return true;
            }
        });
        MyIntentService.setHandler(handler);
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            intent.putExtra("id", photoAdapter.photos.get(position).id);
            intent.putExtra("databaseId", photoAdapter.photos.get(position).databaseId);
            intent.putExtra("browse", photoAdapter.photos.get(position).browseUrl);
            intent.putExtra("title", photoAdapter.photos.get(position).author);
            startActivity(intent);
        }
    };

    void loadAllPhotos() {
        if (checkInternetConnection()) {
            Intent servIntent = new Intent(this, MyIntentService.class);
            servIntent.putExtra("page", currentPage);
            startService(servIntent);
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, R.string.check_your_connection, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Provider.PHOTOS_CONTENT_URI, null, MyContentProvider.PHOTO_KEY_PAGE + " = " + currentPage, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        try {
            if (cursor.getCount() != 0) {
                photoAdapter.photos.clear();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    byte[] image = cursor.getBlob(4);
                    String id = cursor.getString(2);
                    MyPhoto photo = new MyPhoto(id, name, image);
                    photo.fullUrl = cursor.getString(3);
                    photo.databaseId = cursor.getInt(0);
                    photo.browseUrl = cursor.getString(8);
                    photoAdapter.photos.add(photo);
                }
                photoAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        photoAdapter = new PhotoAdapter(new ArrayList<MyPhoto>());
        gridView.setAdapter(photoAdapter);
    }

    public void nextPage(View view) {
        photosShown = 0;
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.next_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.next_out));
        viewFlipper.showNext();

        currentPage++;
        if ((currentPage & 1) == 0) {
            gridView = (GridView) findViewById(R.id.gridView2);
        } else {
            gridView = (GridView) findViewById(R.id.gridView1);
        }
        photoAdapter = new PhotoAdapter(new ArrayList<MyPhoto>());
        gridView.setAdapter(photoAdapter);
        gridView.setOnItemClickListener(listener);
        photoAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(1, null, MainActivity.this);
        loadAllPhotos();
    }

    public void previousPage(View view) {
        if (currentPage == 1) {
            return;
        }
        photosShown = 0;
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.prev_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.prev_out));
        viewFlipper.showPrevious();

        currentPage--;
        if ((currentPage & 1) == 0) {
            gridView = (GridView) findViewById(R.id.gridView2);
        } else {
            gridView = (GridView) findViewById(R.id.gridView1);
        }
        photoAdapter = new PhotoAdapter(new ArrayList<MyPhoto>());
        gridView.setAdapter(photoAdapter);
        gridView.setOnItemClickListener(listener);
        photoAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(1, null, MainActivity.this);
        loadAllPhotos();
    }

    public void refreshPage(View view) {
        if (checkInternetConnection() && !updating) {
            Intent intentService = new Intent(this, MyIntentService.class);
            intentService.putExtra("update", true);
            intentService.putExtra("page", currentPage);
            startService(intentService);
            updating = true;
            photosShown = 0;
        }
    }
}
