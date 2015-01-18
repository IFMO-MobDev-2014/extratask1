package ru.ifmo.zakharvoit.extratask1.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class PagerAdapter extends FragmentPagerAdapter {
    public static final int ELEMENTS_ON_PAGE = 15;
    private final int size;

    public PagerAdapter(FragmentManager fm, int size) {
        super(fm);

        this.size = size;
    }

    @Override
    public Fragment getItem(int position) {
        return ImagesGridFragment.newInstance(position * ELEMENTS_ON_PAGE,
                (position + 1) * ELEMENTS_ON_PAGE);
    }

    @Override
    public int getCount() {
        return size / ELEMENTS_ON_PAGE + (size % ELEMENTS_ON_PAGE != 0 ? 1 : 0);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Title";
    }
}

