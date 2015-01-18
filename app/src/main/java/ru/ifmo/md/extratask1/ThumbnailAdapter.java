package ru.ifmo.md.extratask1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pinguinson on 16.01.2015.
 */
public class ThumbnailAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private GridView gallery;
    private List<Photo> photos;
    private PhotoCacher cacher;
    private Context context;

    public ThumbnailAdapter(GridView gallery, Context ctx) {
        this.photos = new ArrayList<>();
        this.cacher = new PhotoCacher(ctx);
        this.gallery = gallery;
        this.context = ctx;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public int getCount() {
        return photos.size();
    }

    public Object getItem(int position) {
        return photos.get(position).getFullURL();
    }

    public long getItemId(int position) {
        return position / gallery.getNumColumns();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView imageView = (SquareImageView) convertView;
        if (imageView == null) {
            imageView = (SquareImageView) inflater.inflate(R.layout.grid_image, parent, false);
        }
        String url = photos.get(position).getFullURL();

        String resId = new File(url).getName();
        if (cacher.isAvailable(resId)) {
            cacher.putToImageView(imageView, resId);
            imageView.setOnClickListener(new PhotoClickListener(context, url));
        } else {
            imageView.setImageResource(R.drawable.image_error);
        }

        return imageView;
    }
}
