package com.android.ilya.extratask1;

/**
 * Created by ilya4544 on 29.09.2014.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context myContext;
    private List<Bitmap> image;

    public ImageAdapter(Context context) {
        myContext = context;
    }

    public ImageAdapter(Context сontext, List<Bitmap> images) {
        myContext = сontext;
        this.image = images;
    }

    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public Object getItem(int position) {
        return image.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView view = new ImageView(myContext);
        view.setImageBitmap(image.get(position));
        view.setPadding(25, 25, 25, 25);
        view.setLayoutParams(new ListView.LayoutParams(400, 400));
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myContext, FullscreenView.class);
                intent.putExtra("position", position);
                for (Integer i = 0; i < 6; i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.get(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra(i.toString(),byteArray);
                }
                intent.putExtra("page", ((MainActivity)myContext).pager.getCurrentItem());
                myContext.startActivity(intent);
                }
            });
        return view;
    }
}