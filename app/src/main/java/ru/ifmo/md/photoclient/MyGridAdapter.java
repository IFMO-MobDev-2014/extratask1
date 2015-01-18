package ru.ifmo.md.photoclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import java.util.List;

/**
 * Created by Шолохов on 17.01.2015.
 */
public class MyGridAdapter extends ArrayAdapter<Bitmap> {
    List<Bitmap> localData;
    int pageNumber;

    public MyGridAdapter(Context context, List<Bitmap> resource, int startedImage) {
        super(context, R.layout.page_element, resource);
        this.localData = resource;
        this.pageNumber = startedImage;
    }

    public View getView(final int pos, View convertView, ViewGroup parent) {
        View pageElement = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_element, parent, false);
        MyImageView imgView = (MyImageView)pageElement.findViewById(R.id.my_image_view);
        Bitmap curr = localData.get(pos);
        imgView.setImageBitmap(curr);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenActivity = new Intent(getContext(), FullScreenActivity.class);
                fullScreenActivity.putExtra("number", pageNumber + pos + 1);
                Log.d("SENT ", (pageNumber + pos + 1) + "");
                getContext().startActivity(fullScreenActivity);
            }
        });
        return pageElement;
    }
}
