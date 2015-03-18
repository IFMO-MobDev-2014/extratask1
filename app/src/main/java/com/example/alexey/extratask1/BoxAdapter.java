package com.example.alexey.extratask1;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class BoxAdapter extends SimpleCursorAdapter {

    private int layout;

    public BoxAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to) {
        super(_context, _layout, _cursor, _from, _to);
        layout = _layout;
    }

    @Override
    public void bindView(View view, Context _context, Cursor _cursor) {
        Bitmap bitmap = ImageConverter.getImage(_cursor.getBlob(_cursor.getColumnIndex(provider.DATE)));
        ((ImageView) view).setImageBitmap(bitmap);
        view.setLayoutParams(new GridView.LayoutParams(MainActivity.width / 3, MainActivity.width / 3));
        view.setPadding(8, 8, 8, 8);
    }

    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;
    }

}