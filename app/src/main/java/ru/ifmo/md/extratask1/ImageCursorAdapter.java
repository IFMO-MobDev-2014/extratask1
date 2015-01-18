package ru.ifmo.md.extratask1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Mikhail on 18.01.15.
 */
public class ImageCursorAdapter extends CursorAdapter {
    public ImageCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final String myId = cursor.getString(cursor.getColumnIndex("my_id"));
        final String fullSizeLink = cursor.getString(cursor.getColumnIndex("full_size_link"));
        final String page = cursor.getString(cursor.getColumnIndex("page"));

        ImageButton image = (ImageButton) view.findViewById(R.id.picture);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), loadImageFromInternalStorage(context, myId));
        image.setBackgroundDrawable(bitmapDrawable);

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(context, ViewImage.class);
                intent.putExtra("full_size_link", fullSizeLink);
                intent.putExtra("my_id", myId);
                intent.putExtra("page", page);
                context.startActivity(intent);
            }
        });
    }

    public LinkAndId get(int position) {
        Cursor cursor = getCursor();
        LinkAndId linkAndId = new LinkAndId();

        if(cursor.moveToPosition(position)) {
            linkAndId.setFullSizeLink(cursor.getString(cursor.getColumnIndex("full_size_link")));
            linkAndId.setMyId(cursor.getString(cursor.getColumnIndex("my_id")));
        }

        return linkAndId;
    }

    public Bitmap loadImageFromInternalStorage(Context context, String id) {
        Bitmap bitmap = null;
        try {
            FileInputStream fis = context.openFileInput(id);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
