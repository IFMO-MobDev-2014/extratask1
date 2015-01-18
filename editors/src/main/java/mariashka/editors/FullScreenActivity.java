package mariashka.editors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariashka on 1/17/15.
 */
public class FullScreenActivity extends FragmentActivity implements MemExecutable{
    FullScreenFragmentAdapter adapter;
    List<PhotoItem> list = new ArrayList<>();
    int idx;
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapter = new FullScreenFragmentAdapter(getSupportFragmentManager());
        Intent intent = this.getIntent();
        if (savedInstanceState == null)
            idx = intent.getIntExtra("page", 0);
        else
            idx = savedInstanceState.getInt("page");
        MemoryExecuter exec = new MemoryExecuter(this);
        exec.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("page", vpPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }


    @Override
    public void notifyGrid(List<PhotoItem> i) {
        list = i;
        adapter.setList(list);
        vpPager.setAdapter(adapter);
        vpPager.setCurrentItem(idx);
    }
}
