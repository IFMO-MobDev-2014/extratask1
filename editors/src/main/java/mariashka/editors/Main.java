package mariashka.editors;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mariashka.editors.provider.photos.PhotosColumns;
import mariashka.editors.provider.photos.PhotosCursor;


public class Main extends ActionBarActivity implements MemExecutable{
    private List<PhotoItem> items = new ArrayList<>();
    public LoaderExecuter execLoad;
    public MemoryExecuter execMem;
    GridView gridView;
    GridAdapter adapter;
    public static boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execMem = new MemoryExecuter(this);
        if (!isUpdating)
            execMem.execute();
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new GridAdapter(this, R.layout.img_view, items);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                intent.putExtra("page", position);
                startActivity(intent);
            }
        });

        execLoad = new LoaderExecuter(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        execLoad.execute();
    }


    @Override
    public void notifyGrid(List<PhotoItem> i) {
        adapter.clear();
        for (int j = 0; j < i.size(); j++) {
            adapter.add(i.get(j));
        }
        adapter.notifyDataSetChanged();
    }
}
