package mariashka.editors;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.util.List;

import mariashka.editors.provider.photos.PhotosColumns;
import mariashka.editors.provider.photos.PhotosContentValues;
import mariashka.editors.provider.photos.PhotosSelection;

/**
 * Created by mariashka on 1/16/15.
 */
public class LoaderExecuter implements PhotoLoadListener {
    ActionBarActivity main;
    PhotoLoader loader;
    MessageFragment fragment;

    public LoaderExecuter(ActionBarActivity main) {
       this.main = main;
    }

    public void execute() {
        FragmentManager fm = main.getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("photo");
        if (prev != null) {
           ft.remove(prev);
        }
        loader = new PhotoLoader(main.getApplicationContext());
        fragment = new MessageFragment(loader);
        fragment.setLoadListener(this);
        fragment.setCancelable(true);

        Bundle args = new Bundle();
        args.putInt("message", 0);
        fragment.setArguments(args);

        fragment.show(fm, "photo");
    }

    @Override
    public void onLoadFinished(List<PhotoItem> data) {
        if (data == null) {
            Toast.makeText(main.getApplicationContext(),
                    "Can't connect to the server", Toast.LENGTH_LONG).show();

            Toast.makeText(main.getApplicationContext(),
                    "Please check your Internet connection", Toast.LENGTH_LONG).show();
            return;
        }
        storeData(data);
        ((Main) main).notifyGrid(data);


        Toast.makeText(main.getApplicationContext(),
                "Loading finished", Toast.LENGTH_LONG).show();
        Toast.makeText(main.getApplicationContext(),
                "Old and new photos both are available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelLoad(List<PhotoItem> data) {
        ((Main) main).notifyGrid(data);
        storeData(data);

        Toast.makeText(main.getApplicationContext(),
                "Loading was canceled by user", Toast.LENGTH_LONG).show();
        Toast.makeText(main.getApplicationContext(),
                "To get all start sync again", Toast.LENGTH_SHORT).show();
    }

    private void storeData(List<PhotoItem> items) {
        PhotosContentValues contentValues;
        PhotoItem p;
        for (int i = 1; i <= items.size(); i++) {
            p = items.get(items.size() - i);
            PhotosSelection where = new PhotosSelection();
            where.name(p.name);
            Cursor c = main.getContentResolver().query(PhotosColumns.CONTENT_URI,
                    null, where.sel(), where.args(), null);
            c.moveToFirst();
            if (c.isAfterLast()) {
                contentValues = new PhotosContentValues();
                contentValues.putName(p.name).putSmallImg(p.smallImg).putBigImg(p.bigImg)
                        .putDescr(p.descr).putAuthor(p.author).putFace(p.face);
                main.getContentResolver().insert(PhotosColumns.CONTENT_URI, contentValues.values());
            }
        }
    }
}
