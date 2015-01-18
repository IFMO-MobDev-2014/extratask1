package mariashka.editors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by mariashka on 1/18/15.
 */
public class MainAdapter extends FragmentPagerAdapter {
    private List<GridPhotoFragment> fragments;

    public MainAdapter(FragmentManager fm, List<GridPhotoFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Integer.toString(position*20+1) +"-" +Integer.toString(position*20+20);
    }
}
