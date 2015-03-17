package com.example.alexey.extratask1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Alexey on 18.01.2015.
 */

    public class BoxAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<Bitmap> objects;
    private Context mContext;
        BoxAdapter(Context context, ArrayList<Bitmap> products) {
            ctx = context;
            objects = products;
           mContext = context;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return objects.size();
        }


        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(MainActivity.width/3,MainActivity.width/3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setImageBitmap(getProduct(position));
            return imageView;
        }

        Bitmap getProduct(int position) {
            return (Bitmap) getItem(position);
        }

}
