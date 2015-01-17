package ru.ifmo.md.flickrclient;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

/**
 * Created by sultan on 15.01.15.
 */

public class GridAdapter extends CursorAdapter {

    public GridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.row_grid, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        byte[] bytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DBFlickr.PHOTO));
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ImageView imageView = (ImageView) view.findViewById(R.id.small_image);
        imageView.setImageBitmap(bitmap);
    }
}
