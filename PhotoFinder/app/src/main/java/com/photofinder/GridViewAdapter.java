package com.photofinder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Image> pictures;
    private int screenWidth;
    private boolean portrait;

    public GridViewAdapter(Context context, ArrayList<Image> pictures, int screenWidth, boolean portrait) {
        this.context = context;
        this.pictures = pictures;
        this.screenWidth = screenWidth;
        this.portrait = portrait;
    }

    public int getCount() {
        return pictures.size();
    }

    public Object getItem(int position) {
        return pictures.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        int pictureWidth;
        pictureWidth = portrait ? (int) (screenWidth * 0.3) : (int) (screenWidth * 0.14);
        imageView = convertView == null ? new ImageView(context) : (ImageView) convertView;
        imageView.setLayoutParams(new GridView.LayoutParams(pictureWidth, pictureWidth));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(pictures.get(position).bitmap);
        return imageView;
    }
}
