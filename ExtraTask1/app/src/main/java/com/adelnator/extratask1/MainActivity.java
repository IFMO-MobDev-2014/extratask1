package com.adelnator.extratask1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private boolean isPortrait;
    private int width, height;
    private GridView gridView;
    private MyAdapter adapter;
    private DataBase database;
    private List<Bitmap> list, smallList;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        smallList = new ArrayList<Bitmap>();
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        gridView = (GridView) findViewById(R.id.gridView);
        if (isPortrait) {
            gridView.setNumColumns(3);
            gridView.setColumnWidth((int) (dimension.widthPixels * 0.333));
            width = (int)(dimension.widthPixels * 0.333);
            height = width;
        } else {
            gridView.setNumColumns(4);
            gridView.setColumnWidth((int) (dimension.widthPixels * 0.25));
            width = (int)(dimension.widthPixels * 0.25);
            height = width;
        }
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);

        database = new DataBase(this);
        database.open();
        list = database.getAllPicturesData();

        if (list.isEmpty()) {
            Toast.makeText(this, "There are no pictures to view", Toast.LENGTH_SHORT).show();
        }

        smallList.clear();
        for (int i = 0; i < list.size(); i++)
            smallList.add(getSmallBitmap(list.get(i)));

        adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, smallList);
        gridView.setAdapter(adapter);
        button = (Button) findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImages();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), BigPicture.class);
                intent.putExtra("bitmap", i);
                startActivity(intent);
            }
        });
    }

    private Bitmap getSmallBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public void getImages() {
        Toast.makeText(this, "Loading pictures started", Toast.LENGTH_SHORT).show();

        database.deleteAllChannels();
        new Download() {
            @Override
            protected void onPostExecute(List<Picture> bitmaps) {
                if (bitmaps.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Fail internet connection", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                list.clear();

                for (int i = 0; i < bitmaps.size(); i++) {
                    list.add(bitmaps.get(i).bitmap);
                    database.addChannel(bitmaps.get(i).width, bitmaps.get(i).height, bitmaps.get(i).bitmap);
                }
                smallList.clear();
                for (int i = 0; i < list.size(); i++) {
                    smallList.add(getSmallBitmap(list.get(i)));
                }

                adapter.notifyDataSetChanged();
                Toast toast = Toast.makeText(getApplicationContext(), "Pictures are loaded and saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        }.execute();
    }

    public class MyAdapter extends ArrayAdapter<Bitmap> {

        public MyAdapter(Context context, int textViewResourceId, List<Bitmap> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = new ImageView(getContext());
            view.setImageBitmap(getItem(position));
            return view;
        }
    }

}