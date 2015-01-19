package ru.ifmo.instafeed.ui.feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.ifmo.instafeed.R;
import ru.ifmo.instafeed.rest.model.Item;

/**
 * Created by creed on 18.01.15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<Item> items;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {

        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView)convertView;
        }

        Item currentItem = items.get(position);

        if (currentItem.getType().equals("image")) {
            Picasso.with(context)
                    .load(currentItem.getImages().getThumbnail().getUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit().centerCrop()
                    //.noFade()
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(R.drawable.placeholder)
                    //.resize(150, 150)
                    //.fit().centerCrop()
                    .noFade()
                    .into(imageView);
        }
        return imageView;
    }
}