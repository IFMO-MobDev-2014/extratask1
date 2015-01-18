package com.pokrasko.extratask1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent,
                false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.previewImageView);
        imageView.setImageBitmap(resource.get(position));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FullActivity.class);
                intent.putExtra("index", number + position);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    /*private Bitmap squarize(Bitmap bitmap) {
        int size = bitmap.getWidth();
        if (bitmap.getHeight() <= size) {
            return bitmap;
        } else {
            Canvas canvas = new Canvas();
            Rect rect = new Rect(0, 0, size, size);
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }*/
}
