package com.example.izban.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
    public ImagesAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = new ImageView(getContext());
        }
        try {
            Log.i("", getItem(position).toString());
            FileInputStream inputStream = getContext().openFileInput(getItem(position).filePath);
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            ((ImageView)view).setImageBitmap(image);
            inputStream.close();
        } catch (IOException e) {

        }
        return view;
    }
}
