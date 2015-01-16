package mariashka.editors.loader;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.widget.Toast;

import java.util.List;

import mariashka.editors.Main;
import mariashka.editors.provider.photos.PhotosColumns;
import mariashka.editors.provider.photos.PhotosContentValues;
import mariashka.editors.provider.photos.PhotosSelection;

/**
 * Created by mariashka on 1/16/15.
 */
public class LoaderExecuter implements PhotoLoadListener {
    Activity main;
    PhotoLoader loader;
    MessageFragment fragment;

    public LoaderExecuter(Activity main) {
       this.main = main;
    }

    public void execute() {
        FragmentManager fm = main.getFragmentManager();

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

        fragment.show(ft, "photo");
    }

    @Override
    public void onLoadFinished(List<PhotoItem> data) {
        storeData(data);
        ((Main) main).notifyGrid(data);

        Toast toast = new Toast(main.getApplicationContext());
        toast.setText("Sync finished");
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        toast.setText("Old and new photos are available to you");
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onCancelLoad(List<PhotoItem> data) {
        storeData(data);
        ((Main) main).notifyGrid(data);

        Toast toast = new Toast(main.getApplicationContext());
        toast.setText("Loading was canceled by user");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        toast.setText("To get all start sync again");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
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
            if (c == null) {
                contentValues = new PhotosContentValues();
                contentValues.putName(p.name).putSmallImg(p.smallImg).putBigImg(p.bigImg)
                        .putDescr(p.descr).putAuthor(p.author).putFace(p.face);
                main.getContentResolver().insert(PhotosColumns.CONTENT_URI, contentValues.values());
            }
        }
    }
}
