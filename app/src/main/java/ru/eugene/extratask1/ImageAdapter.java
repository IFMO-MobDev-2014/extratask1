package ru.eugene.extratask1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.eugene.extratask1.db.ImageItem;

/**
 * Created by eugene on 1/18/15.
 */
public class ImageAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<ImageItem> images;
    private LayoutInflater inflater;
    private int position;

    public ImageAdapter(Context context, int resource, List<ImageItem> images) {
        super(context, resource);
        this.images = (ArrayList<ImageItem>) images;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (images.size() < position) return convertView;
        this.position = position;
        if (convertView == null)
            convertView = inflater.inflate(R.layout.image_layout, parent, false);
        ImageView picture = (ImageView) convertView;

        picture.setImageURI(Uri.parse(images.get(position).getThumbnail()));

        return convertView;
    }
}
