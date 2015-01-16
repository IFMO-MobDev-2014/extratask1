package mariashka.editors;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import mariashka.editors.loader.LoaderExecuter;
import mariashka.editors.loader.PhotoItem;


public class Main extends ActionBarActivity {

    private List<PhotoItem> items;
    LoaderExecuter exec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exec = new LoaderExecuter(this);
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

    public void notifyGrid(List<PhotoItem> i) {
        setItems(i);
    }
}
