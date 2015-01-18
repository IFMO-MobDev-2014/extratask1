package freemahn.com.extratask1;

/**
 * Created by Freemahn on 16.01.2015.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context myContext;
    Entry [] entries;

    public ImageAdapter(Context c) {
        myContext = c;
    }

    public ImageAdapter(Context сontext, List<Entry> images) {
        myContext = сontext;
        this.entries = images.toArray(new Entry[images.size()]);
    }

    @Override
    public int getCount() {
        return entries.length;
    }

    @Override
    public Object getItem(int position) {
        return entries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = new ImageView(myContext);
        view.setImageBitmap(entries[position].image);
        int padding = 5;
        view.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(new ListView.LayoutParams(250, 250));
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        return view;
    }
}
