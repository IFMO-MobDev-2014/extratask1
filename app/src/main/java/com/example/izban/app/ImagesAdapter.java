package com.example.izban.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            view = new TextView(getContext());
        }
        ((TextView)view).setText("1");
        return view;
    }
}
