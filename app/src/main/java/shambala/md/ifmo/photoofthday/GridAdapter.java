package shambala.md.ifmo.photoofthday;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 107476 on 10.01.2015.
 */
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
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        SquareImageView imageView = (SquareImageView) convertView.findViewById(R.id.imageView);
        imageView.setImageBitmap(resource.get(position));
        final int pos = position;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OneImage.class);
                intent.putExtra("index", number+position);
                intent.putExtra("updated", MainActivity.updated[position]);
                MainActivity.updated[position] = true;
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
