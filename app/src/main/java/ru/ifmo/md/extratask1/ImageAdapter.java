package ru.ifmo.md.extratask1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Svet on 15.01.2015.
 */
public class ImageAdapter extends BaseAdapter {

    private ArrayList<ImageContainer> containers;

    public ImageAdapter(ArrayList<ImageContainer> containers) {
        this.containers = containers;
    }
    @Override
    public int getCount() {
        return containers.size();
    }

    @Override
    public Object getItem(int i) {
        return containers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return containers.get(i).mainView;
    }
}
