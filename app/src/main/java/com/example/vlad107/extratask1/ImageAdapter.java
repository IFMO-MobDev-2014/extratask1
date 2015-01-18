package com.example.vlad107.extratask1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<ImageEntry> list;

    public ImageAdapter(Context context, List<ImageEntry> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = new ImageView(context);
            ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER);
        }
        ((ImageView)view).setImageBitmap(list.get(position).image);
        return view;
    }
}