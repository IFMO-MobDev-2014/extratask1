package ru.ifmo.ctddev.filippov.extratask1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Dima_2 on 01.03.2015.
 */
public class PhotoAdapter extends BaseAdapter {
    public List<MyPhoto> photos;

    public PhotoAdapter(List<MyPhoto> data) {
        this.photos = data;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = LayoutInflater.from(parent.getContext()).inflate(R.layout.cellgrid, parent, false);
        ImageView imageView = (ImageView) cell.findViewById(R.id.imagepart);
        TextView textView = (TextView) cell.findViewById(R.id.textpart);
        imageView.setImageBitmap(photos.get(position).getBitmap());
        textView.setText(photos.get(position).author);
        return cell;
    }

}