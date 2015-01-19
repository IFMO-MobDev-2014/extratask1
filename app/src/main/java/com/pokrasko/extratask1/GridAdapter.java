package com.pokrasko.extratask1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

public class GridAdapter extends ArrayAdapter<Bitmap> {
    List<Bitmap> resource;
    int number;

    public GridAdapter(Context context, List<Bitmap> resource, int number) {
        super(context, R.layout.grid_element, resource);
        this.resource = resource;
        this.number = number;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent,
                false);
        SquareImageView imageView = (SquareImageView) convertView.findViewById(R.id.previewImageView);
        imageView.setImageBitmap(resource.get(position));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageUpdater.running) {
                    Toast.makeText(parent.getContext(), "Images are being refreshed",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), FullActivity.class);
                intent.putExtra("index", number + position);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
