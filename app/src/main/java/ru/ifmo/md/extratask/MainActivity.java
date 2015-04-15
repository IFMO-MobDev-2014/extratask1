package ru.ifmo.md.extratask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class MainActivity extends Activity {
    public static final int NUMBER_TO_LOAD = 50;
    public static final String EXTRA_IDS = "ids";
    public static final String EXTRA_URLS = "urls";
    public static final String EXTRA_ORIGS = "origs";
    public static final String EXTRA_LARGES = "larges";
    public static final String EXTRA_PREVIEWS = "previews";
    public static final String EXTRA_PICTURES = "pictures";
    public static final String EXTRA_POSITION = "position";
    private ArrayList<Photo> photos;
    private ImageAdapter adapter;
    private boolean firstRun;
    private Context context;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photos = new ArrayList<>(NUMBER_TO_LOAD);
        setContentView(R.layout.activity_main);
        context = this;
        helper = new DBHelper(this);

        if (savedInstanceState != null) {
            ArrayList<String> ids = savedInstanceState.getStringArrayList(EXTRA_IDS);
            ArrayList<String> urls = savedInstanceState.getStringArrayList(EXTRA_URLS);
            ArrayList<String> origs = savedInstanceState.getStringArrayList(EXTRA_ORIGS);
            ArrayList<String> larges = savedInstanceState.getStringArrayList(EXTRA_LARGES);
            ArrayList<String> previews = savedInstanceState.getStringArrayList(EXTRA_PREVIEWS);
            if (ids != null && previews != null && larges != null) {
                for (int i = 0; i < ids.size(); i++) {
                    photos.add(new Photo(ids.get(i), previews.get(i), larges.get(i), origs.get(i), urls.get(i)));
                }
            }
            firstRun = false;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DBHelper.PHOTOS_TABLE_NAME, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                    String large = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_LARGE));
                    String preview = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PREVIEW));
                    String orig = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ORIG));
                    String url = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_URL));
                    photos.add(new Photo(id, preview, large, orig, url));
                } while (cursor.moveToNext());
            } else {
                firstRun = true;
            }
            cursor.close();
        }

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new GetAllImagesTask(context, adapter, photos, adapter.bitmaps, true, swipeRefreshLayout)).execute();
            }
        });
        adapter = new ImageAdapter(swipeRefreshLayout);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        int rows = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rows = 4;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, rows));
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        public final ArrayList<Bitmap> bitmaps = new ArrayList<>(NUMBER_TO_LOAD);
        final ArrayList<String> ids = new ArrayList<>(NUMBER_TO_LOAD);
        final ArrayList<String> urls = new ArrayList<>(NUMBER_TO_LOAD);
        final ArrayList<String> larges = new ArrayList<>(NUMBER_TO_LOAD);
        final ArrayList<String> origs = new ArrayList<>(NUMBER_TO_LOAD);


        public ImageAdapter(SwipeRefreshLayout swipeRefreshLayout) {
            for (int i = 0; i < NUMBER_TO_LOAD; ++i) {
                bitmaps.add(null);
            }
            for (int i = photos.size(); i < NUMBER_TO_LOAD; i++) {
                photos.add(null);
            }
            new GetAllImagesTask(context, this, photos, bitmaps, firstRun, swipeRefreshLayout).execute();
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, final int position) {
            if (position < photos.size() && photos.get(position) != null) {
                holder.imageView.setImageBitmap(bitmaps.get(position));
                holder.imageView.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Photo photo : photos) {
                        if (photo == null) {
                            break;
                        }
                        ids.add(photo.getId());
                        urls.add(photo.getUrl());
                        origs.add(photo.getOrig());
                        larges.add(photo.getLarge());
                    }

                    Intent intent = new Intent(holder.imageView.getContext(), FullResActivity.class);
                    intent.putExtra(EXTRA_POSITION, position);
                    intent.putStringArrayListExtra(EXTRA_IDS, ids);
                    intent.putStringArrayListExtra(EXTRA_PICTURES, larges);
                    intent.putStringArrayListExtra(EXTRA_ORIGS, origs);
                    intent.putStringArrayListExtra(EXTRA_URLS, urls);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return NUMBER_TO_LOAD;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<String> ids = new ArrayList<>(NUMBER_TO_LOAD);
        ArrayList<String> urls = new ArrayList<>(NUMBER_TO_LOAD);
        ArrayList<String> origs = new ArrayList<>(NUMBER_TO_LOAD);
        ArrayList<String> larges = new ArrayList<>(NUMBER_TO_LOAD);
        ArrayList<String> previews = new ArrayList<>(NUMBER_TO_LOAD);

        for (Photo photo : photos) {
            if (photo != null) {
                ids.add(photo.getId());
                urls.add(photo.getUrl());
                origs.add(photo.getOrig());
                larges.add(photo.getLarge());
                previews.add(photo.getPreview());
            }
        }
        outState.putStringArrayList(EXTRA_IDS, ids);
        outState.putStringArrayList(EXTRA_URLS, urls);
        outState.putStringArrayList(EXTRA_ORIGS, origs);
        outState.putStringArrayList(EXTRA_LARGES, larges);
        outState.putStringArrayList(EXTRA_PREVIEWS, previews);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        (new DbSaveTask(helper, photos)).execute();
    }
}