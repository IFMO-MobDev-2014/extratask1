package mariashka.editors;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main extends ActionBarActivity implements MemExecutable{
    private List<PhotoItem> items = new ArrayList<>();
    public LoaderExecuter execLoad;
    public MemoryExecuter execMem;
    MainAdapter adapter;
    LayoutInflater inflater;
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = LayoutInflater.from(this);
        vpPager = (ViewPager) findViewById(R.id.pager);
        execLoad = new LoaderExecuter(this);
        execMem = new MemoryExecuter(this);
        execMem.execute();
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

    public void syncOnClick(MenuItem item) {
        execLoad.execute();
    }

    public void onGridItemClick(GridView g, View v, int position, long id) {
        Intent intent = new Intent(this, FullScreenActivity.class);
        intent.putExtra("page", position);
        startActivity(intent);
    }

    @Override
    public void notifyGrid(List<PhotoItem> i) {
        if (i.size() == 0)
            Toast.makeText(this, "Press sync to load photos", Toast.LENGTH_LONG).show();
        items = i;
        List<GridPhotoFragment> fragments = new ArrayList<>();
        for (int j = 0; j < i.size(); j += 20) {
            fragments.add(new GridPhotoFragment(i.subList(j, Math.min(j + 20, i.size())), this, j));
        }
        adapter = new MainAdapter(getSupportFragmentManager(), fragments);
        vpPager.setAdapter(adapter);
    }
}
