package com.example.picturemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Амир on 17.01.2015.
 */
public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<MyImage> images;

    public ImageAdapter(Context context, ArrayList<MyImage> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.small_image);
        imageView.setImageBitmap(images.get(position).image);
        return convertView;
    }
}
