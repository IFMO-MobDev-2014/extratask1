package com.example.heat_wave.photoviewer.auxiliary;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heat_wave.photoviewer.R;

import java.util.Collection;
import java.util.List;

/**
 * Created by heat_wave on 14.01.15.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private List<Bitmap> photoList;

    public PhotoAdapter(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, int i) {
        Bitmap temp = photoList.get(i);
        photoViewHolder.photo.setImageBitmap(temp);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new PhotoViewHolder(itemView);
    }

    public void clear() {
        photoList.clear();
    }

    public void addAll(Collection<Bitmap> c) {
        photoList.addAll(c);
    }

}