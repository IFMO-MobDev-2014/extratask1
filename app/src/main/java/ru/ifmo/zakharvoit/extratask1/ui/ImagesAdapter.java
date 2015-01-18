package ru.ifmo.zakharvoit.extratask1.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import ru.ifmo.zakharvoit.extratask1.R;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureCursor;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class ImagesAdapter extends CursorAdapter {
    public ImagesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.image_cell, null);
        bindView(view, context, cursor);
        return view;
    }

    @Override
    public void bindView(View _view, Context context, Cursor _cursor) {
        PictureCursor cursor = new PictureCursor(_cursor);
        ImageView view = (ImageView) _view;

        byte[] contents = cursor.getContents();
        view.setImageBitmap(BitmapFactory.decodeByteArray(contents, 0, contents.length));
    }
}
