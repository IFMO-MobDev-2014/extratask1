package ru.ifmo.md.extratask1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private GridView gallery;
    private List<String> urls;
    private ImageCacher cacher;
    private Context context;

    public ImageAdapter(GridView gallery, Context ctx) {
        this.urls = new ArrayList<>();
        this.cacher = new ImageCacher(ctx);
        this.gallery = gallery;
        this.context = ctx;
    }

    public void setData(List<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    public int getCount() {
        return urls.size();
    }

    public Object getItem(int position) {
        return urls.get(position);
    }

    public long getItemId(int position) {
        return position / gallery.getNumColumns();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (SquareImageView) LayoutInflater.from(context).inflate(R.layout.grid_image, null);
        String url = urls.get(position);

        String resId = new File(url).getName();
        if (cacher.isAvailable(resId)) {
            cacher.putToImageView(imageView, resId);
            imageView.setOnClickListener(new ImageClickListener(context, url));
        } else {
            imageView.setImageResource(R.drawable.image_error);
        }

        return imageView;
    }
}
