package daria.extratask;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


public class PhotosActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView.Adapter photoAdapter;

    RecyclerView photoView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Bitmap> thumbnailList;
    ProgressBar progressBar;
    PhotoDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PhotoDBHelper(this);
        setContentView(R.layout.activity_photos);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        photoView = (RecyclerView) findViewById(R.id.imageList);
        photoView.setHasFixedSize(true);


        RecyclerView.LayoutManager photoLayoutManager = new GridLayoutManager(this, 3);
        photoView.setLayoutManager(photoLayoutManager);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(30);
        thumbnailList = new ArrayList<>();

        photoView.addItemDecoration(new ItemDecoration(30));

        if (db.getPhotosCount() < 30) {
            new PhotoSearchTask(this).execute();
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            for (int i = 1; i <= 30; i++) {
                thumbnailList.add(db.getImageFull(i));
            }


            photoAdapter = new PhotoAdapter(thumbnailList);
            photoView.setAdapter(photoAdapter);
        }



    }

    public void onSearchFinished(ArrayList<Photo> photos) {
        for (Photo photo : photos) {
            new DownloadImagesTask(this).execute(photo);
        }
    }

    public void onImageDownloaded(Photo photo) {
        db.addPhoto(photo);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setProgress(progressBar.getProgress() + 1);
        }
        if (db.getPhotosCount() == 30) {
            progressBar.setVisibility(View.INVISIBLE);

            for (int i = 1; i <= 30; i++) {
                thumbnailList.add(db.getImageFull(i));
            }

            photoAdapter = new PhotoAdapter(thumbnailList);
            photoView.setAdapter(photoAdapter);
        }


    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setProgress(0);
        db.onUpgrade(db.getWritableDatabase(), 1, 2);
        thumbnailList.clear();
        new PhotoSearchTask(this).execute();
        for (int i = 1; i <= 30; i++) {
            thumbnailList.add(db.getImageFull(i));
        }
        photoAdapter = new PhotoAdapter(thumbnailList);
        photoAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
        progressBar.setProgress(0);
    }

    public void watchFullImage(View v) {
        ImageView photo = (ImageView) v;
        Bitmap compare =((BitmapDrawable)photo.getDrawable()).getBitmap();
        int pos = 0;
        for (int i = 0; i < 30; i++) {
            if (compare.equals(thumbnailList.get(i))) {
                pos = i;
                break;
            }
        }
        Intent i = new Intent(this, FullImageActivity.class);
        i.putExtra("POSITION", pos);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
