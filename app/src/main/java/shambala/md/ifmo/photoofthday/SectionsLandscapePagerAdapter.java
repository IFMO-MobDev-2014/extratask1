package shambala.md.ifmo.photoofthday;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by 107476 on 12.01.2015.
 */
public class SectionsLandscapePagerAdapter extends FragmentPagerAdapter {



    public SectionsLandscapePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        return MainActivity.PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 6 total pages.
        return 6;
    }
}
