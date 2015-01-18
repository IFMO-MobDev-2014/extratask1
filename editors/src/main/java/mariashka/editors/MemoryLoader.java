package mariashka.editors;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mariashka.editors.provider.photos.PhotosColumns;
import mariashka.editors.provider.photos.PhotosCursor;

/**
 * Created by mariashka on 1/17/15.
 */
public class MemoryLoader extends AsyncTaskLoader<List<PhotoItem>> {
    private boolean canceled = false;
    private List<PhotoItem> list = new ArrayList<>();
    Context context;

    protected MemoryLoader(Context context) {
        super(context);
        this.context = context;
    }

    public List<PhotoItem> getList() {
        return list;
    }

    @Override
    public List<PhotoItem> loadInBackground() {
        try {
            Cursor c = context.getContentResolver().query(PhotosColumns.CONTENT_URI,
                    null, null, null, null);
            List<PhotoItem> list = new ArrayList<>();
            c.moveToFirst();
            if (c.isAfterLast()) {
                return list;
            }
            PhotosCursor cursor = new PhotosCursor(c);
            while (!cursor.isAfterLast()) {
                PhotoItem curr = new PhotoItem(cursor.getName(), cursor.getSmallImg(), cursor.getBigImg());
                list.add(0, curr);
                cursor.moveToNext();
            }
            cursor.close();
            Log.d("LISTLISTLIST00", "" + list.size());
            return list;
        } catch (NullPointerException e1) {
            return new ArrayList<>();
        } catch (RuntimeException e2) {
            return new ArrayList<>();
        }
    }


    public Bundle getArguments() {
        return null;
    }

    protected void setHandler(Handler handler) {
    }

    public void setCanceled(boolean cancelled) {
        this.canceled = cancelled;
    }

    public boolean isCancelled() {
        return canceled;
    }
}
