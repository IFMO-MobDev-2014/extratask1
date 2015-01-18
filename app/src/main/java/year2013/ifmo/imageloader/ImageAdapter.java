package year2013.ifmo.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Юлия on 18.01.2015.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<CustomImage> _links;
    private Bitmap bmp;


    public ImageAdapter(Context c, List<CustomImage> links) {
        mContext = c;
        _links = links;
    }

    public void addLink(CustomImage link)
    {
        _links.add(link);
    }

    public void addLinks(List<CustomImage> list)
    {
        _links.addAll(list);
    }

    public void clearList() {_links.clear();}

    @Override
    public int getCount() {
        return _links.size();
    }

    @Override
    public Object getItem(int position) {
        return _links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if (_links.get(position).Bitmap != null)
            imageView.setImageBitmap(_links.get(position).Bitmap);

        return imageView;

    }
}

