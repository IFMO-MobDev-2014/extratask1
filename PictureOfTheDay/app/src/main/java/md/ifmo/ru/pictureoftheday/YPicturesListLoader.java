package md.ifmo.ru.pictureoftheday;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by Илья on 17.01.2015.
 */
public class YPicturesListLoader extends AsyncTaskLoader<ArrayList<YPicture>> {
    Context context;
    Bitmap bmp;
    boolean needed;

    public YPicturesListLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList<YPicture> loadInBackground() {
        ArrayList<YPicture> list = new ArrayList<YPicture>();
        Cursor c = context.getContentResolver().query(
                PicturesContentProvider.PICTURES_URI,
                null,
                null,
                null,
                null
        );

        if (c != null) {
            byte[] byteArray;
            c.moveToFirst();
            YPicture img;
            while (!c.isBeforeFirst() && !c.isAfterLast()) {
                byteArray = c.getBlob(c.getColumnIndex(DBPictures.COLUMN_PICTURE));
                //bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                img = new YPicture(
                        byteArray,
                        c.getString(c.getColumnIndex(DBPictures.COLUMN_PICTURE_HR)),
                        c.getString(c.getColumnIndex(DBPictures.COLUMN_PICTURE_LINK)),
                        c.getString(c.getColumnIndex(DBPictures.COLUMN_PICTURE_TITLE))
                );
                list.add(img);
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
