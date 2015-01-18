package com.example.izban.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by izban on 17.01.15.
 */

public class ImagesAdapter extends ArrayAdapter<MyImage> {
    static class MyImageView extends ImageView {

        public MyImageView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }
    }

    public ImagesAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = new MyImageView(getContext());
        }
        ((MyImageView)view).setScaleType(ImageView.ScaleType.FIT_XY);
        view.setPadding(8, 8, 8, 8);
        try {
            FileInputStream inputStream = getContext().openFileInput(getItem(position).filePath);
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            ((MyImageView)view).setImageBitmap(image);
            inputStream.close();
        } catch (IOException e) {

        }
        return view;
    }
}
