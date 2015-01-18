package me.loskutov.popularphotosviewer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ArrayList<Photo> photos;
    private ImageAdapter adapter;
    private boolean firstRun;
    private Context context;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photos = new ArrayList<>(50);
        setContentView(R.layout.activity_main);
        context = this;
        helper = new DBHelper(this);

        if(savedInstanceState != null) {
            ArrayList<String> ids = savedInstanceState.getStringArrayList("ids");
            ArrayList<String> urls = savedInstanceState.getStringArrayList("urls");
            ArrayList<String> origs = savedInstanceState.getStringArrayList("origs");
            ArrayList<String> larges = savedInstanceState.getStringArrayList("larges");
            ArrayList<String> previews = savedInstanceState.getStringArrayList("previews");
            if(ids != null && previews != null && larges != null) {
                for (int i = 0; i < ids.size(); i++) {
                    photos.add(new Photo(ids.get(i), previews.get(i), larges.get(i), origs.get(i), urls.get(i)));
                }
            }
            firstRun = false;
        } else {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("PHOTOS", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(helper.COLUMN_ID));
                    String large = cursor.getString(cursor.getColumnIndex(helper.COLUMN_LARGE));
                    String preview = cursor.getString(cursor.getColumnIndex(helper.COLUMN_PREVIEW));
                    String orig = cursor.getString(cursor.getColumnIndex(helper.COLUMN_ORIG));
                    String url = cursor.getString(cursor.getColumnIndex(helper.COLUMN_URL));
                    photos.add(new Photo(id, preview, large, orig, url));
                } while (cursor.moveToNext());
            } else {
                firstRun = true;
            }
            cursor.close();
        }

        adapter = new ImageAdapter();

        // Refresh FAB outline and onClick handling
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int diameter = getResources().getDimensionPixelSize(R.dimen.fab_diameter);
                outline.setOval(0, 0, diameter, diameter);
            }
        });
        refreshButton.setClipToOutline(true);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new GetAllImagesTask(context, adapter, photos, adapter.bitmaps, true)).execute();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        int rows = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rows = 4;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, rows));
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        public final ArrayList<Bitmap> bitmaps = new ArrayList<>(50);
        final ArrayList<String> ids = new ArrayList<>(50);
        final ArrayList<String> urls = new ArrayList<>(50);
        final ArrayList<String> larges = new ArrayList<>(50);
        final ArrayList<String> origs = new ArrayList<>(50);


        public ImageAdapter() {
            for (int i = 0; i < 50; ++i) {
                bitmaps.add(null);
            }
            for (int i = photos.size(); i < 50; i++) {
                photos.add(null);
            }
            (new GetAllImagesTask(context, this, photos, bitmaps, firstRun)).execute();

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
                    //TODO: move it to a better place
                    for(Photo photo : photos) {
                        if(photo == null) {
                            break;
                        }
                        ids.add(photo.id);
                        urls.add(photo.url);
                        origs.add(photo.orig);
                        larges.add(photo.large);
                    }

                    Intent intent = new Intent(holder.imageView.getContext(), FullresActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("ids", ids);
                    intent.putStringArrayListExtra("pictures", larges);
                    intent.putStringArrayListExtra("origs", origs);
                    intent.putStringArrayListExtra("urls", urls);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return 50;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<String> ids = new ArrayList<>(50);
        ArrayList<String> urls = new ArrayList<>(50);
        ArrayList<String> origs = new ArrayList<>(50);
        ArrayList<String> larges = new ArrayList<>(50);
        ArrayList<String> previews = new ArrayList<>(50);

        for(Photo photo : photos) {
            if(photo != null) {
                ids.add(photo.id);
                urls.add(photo.url);
                origs.add(photo.orig);
                larges.add(photo.large);
                previews.add(photo.preview);
            }
        }
        outState.putStringArrayList("ids", ids);
        outState.putStringArrayList("urls", urls);
        outState.putStringArrayList("origs", origs);
        outState.putStringArrayList("larges", larges);
        outState.putStringArrayList("previews", previews);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        (new DbSaveTask(helper, photos)).execute();
    }
}