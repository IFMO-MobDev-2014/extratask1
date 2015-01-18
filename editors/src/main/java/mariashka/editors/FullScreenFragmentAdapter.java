package mariashka.editors;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by mariashka on 1/17/15.
 */
public class FullScreenFragmentAdapter extends FragmentPagerAdapter{
    List<PhotoItem> items;
    public FullScreenFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setList(List<PhotoItem> items) {
        this.items = items;
    }

    @Override
    public Fragment getItem(int i) {
        return FullScreenFragment.newInstance(items.get(i).name, items.get(i).bigImg);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).name;
    }
}
