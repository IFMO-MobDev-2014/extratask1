package mariashka.editors;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by mariashka on 1/17/15.
 */
public class MemoryExecuter implements PhotoLoadListener {

    FragmentActivity main;
    MemoryLoader loader;
    SpinnerFragment fragment;

    public MemoryExecuter(FragmentActivity main) {
       this.main = main;
    }

    public void execute() {
        main.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FragmentManager fm = main.getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("photo");
        if (prev != null) {
           ft.remove(prev);
        }
        loader = new MemoryLoader(main.getApplicationContext());
        fragment = new SpinnerFragment(loader);
        fragment.setLoadListener(this);

        Bundle args = new Bundle();
        args.putInt("message", 0);
        fragment.setArguments(args);

        fragment.show(fm, "photo");
    }

    @Override
    public void onLoadFinished(List<PhotoItem> data) {
        ((MemExecutable)main).notifyGrid(data);
        main.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCancelLoad(List<PhotoItem> data) {
        Toast.makeText(main.getApplicationContext(),
                "Loading was canceled by user", Toast.LENGTH_LONG).show();
        main.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
