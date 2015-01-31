package freemahn.com.extratask1;

/**
 * Created by Freemahn on 17.01.2015.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class ImagesListLoader extends AsyncTaskLoader<ArrayList<Entry>> {
    Context context;

    public ImagesListLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList<Entry> loadInBackground() {
        ArrayList<Entry> list = new ArrayList<Entry>();

        Cursor c = context.getContentResolver().query(
                ImagesContentProvider.IMAGES_URI,
                null,
                null,
                null,
                null
        );

        if (c != null) {
            c.moveToFirst();
            while (!c.isBeforeFirst() && !c.isAfterLast()) {
                byte[] byteArray = c.getBlob(c.getColumnIndex(DatabaseHelper.IMAGE_COLUMN));
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Entry e = new Entry();
                e.title = c.getString(c.getColumnIndex(DatabaseHelper.TITLE_COLUMN));
                e.linkSmall = c.getString(c.getColumnIndex(DatabaseHelper.LINK_SMALL_COLUMN));
                e.linkBig = c.getString(c.getColumnIndex(DatabaseHelper.LINK_BIG_COLUMN));
                e.image = bmp;
               // Log.d("LOADER", bmp + "");
                list.add(e);
                c.moveToNext();
            }
        }
        c.close();

        return list;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}