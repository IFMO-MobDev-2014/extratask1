package com.example.maksim.photoview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List <Image> images;

    public ImageAdapter(Context context, List <Image> images) {
        this.context = context;
        this.images = images;
    }

    public void setImages(List <Image> images) {
        this.images = images;
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
        ImageView view;
        if (convertView == null) {
            view = new ImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            view = (ImageView) convertView;
        }
        view.setImageBitmap(images.get(position).getSmallImage());
        return view;
    }
}
