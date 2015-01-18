package md.ifmo.ru.pictureoftheday;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Илья on 17.01.2015.
 */
public class MyPicturesListAdapter extends BaseAdapter {
    private Context context;
    private List<YPicture> list;

    public MyPicturesListAdapter(Context context, List<YPicture> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(list.get(position).bytebitmap, 0, list.get(position).bytebitmap.length));
        return imageView;
    }
}
