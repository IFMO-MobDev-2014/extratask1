package mariashka.editors;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mariashka.editors.provider.photos.PhotosColumns;
import mariashka.editors.provider.photos.PhotosCursor;


public class Main extends ActionBarActivity {

    private List<PhotoItem> items = new ArrayList<>();
    LoaderExecuter exec;
    GridView gridView;
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setFromMem();
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new GridAdapter(this, R.layout.img_view, items);
        gridView.setAdapter(adapter);

        exec = new LoaderExecuter(this);
    }


    public void setFromMem() {
        Cursor c = getContentResolver().query(PhotosColumns.CONTENT_URI,
                null, null, null, null);
        c.moveToFirst();
        if (c.isAfterLast()) {
            Toast.makeText(this, "Start sync to load pictures", Toast.LENGTH_SHORT).show();
            return;
        }
        PhotosCursor cursor = new PhotosCursor(c);
        List<PhotoItem> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            PhotoItem curr = new PhotoItem(cursor.getName(), cursor.getSmallImg(), cursor.getBigImg());
            curr.author = cursor.getAuthor();
            curr.descr = cursor.getDescr();
            curr.face = cursor.getFace();
            list.add(0, curr);
            cursor.moveToNext();
        }
        setItems(list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sync) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<PhotoItem> getItems() {
        return items;
    }

    public void setItems(List<PhotoItem> items) {
        this.items = items;
    }

    public void syncOnClick(MenuItem item) {
        exec.execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

        setFromMem();
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new GridAdapter(this, R.layout.img_view, items);
        gridView.setAdapter(adapter);

        exec = new LoaderExecuter(this);
    }


    public void notifyGrid(List<PhotoItem> i) {
        List<PhotoItem> curr = getItems();
        List<PhotoItem> curr2 = new ArrayList<>();
        if (curr == null) {
            setItems(i);
            return;
        }

        Log.d("size", "" + curr.size());
        for (int j = 0; j < i.size(); j++) {
            boolean f = true;
            for (int k = 0; k < curr.size(); k++) {
                if (i.get(j).name.equals(curr.get(k).name))
                    f = false;
            }
            if (f)
                curr2.add(i.get(j));
        }

        Log.d("Loaded size", "" + i.size());
        for (int k = 0; k < i.size(); k++) {
            Log.d("arrayLength", "" + i.get(k).smallImg.length);
        }

        for (int j = 0; j < curr.size(); j++) {
            boolean f = true;
            for (int k = 0; k < i.size(); k++) {
                if (i.get(k).name.equals(curr.get(j).name))
                    f = false;
            }
            if (f)
                i.add(curr.get(j));
        }
        setItems(i);
        for (int j = 0; j < curr2.size(); j++) {
            adapter.add(curr2.get(j));
        }
        adapter.notifyDataSetChanged();
    }
}
