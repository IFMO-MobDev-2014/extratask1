package ru.ifmo.md.extratask1.photoclient;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;

/**
 * Created by sergey on 17.01.15.
 */
public class GridImageAdapter extends CursorAdapter {

    public GridImageAdapter(Context context, Cursor c, boolean flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.grid_view_one_picture, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String imageURL = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_SMALL_CONTENT_URI));
        Bitmap imageBitmap = ImageFilesHandler.loadImageFromStorage(context, imageURL);

        /*
            Uncomment this if you want to see author's name
            String authorName = "by " + cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_AUTHOR_NAME));
            ((TextView) view.findViewById(R.id.tv_photo_name)).setText(authorName);
        */

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_one_picture);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(imageBitmap);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

}
