package ru.ifmo.md.photoclient;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by Шолохов on 17.01.2015.
 */
public class MySwipePagerAdapter extends FragmentPagerAdapter {
    public int size;

    public MySwipePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.size = size;
    }

    @Override
    public Fragment getItem(int position) {
            return MainActivity.TabFragment.newTab(position, size);

    }

    @Override
    public int getCount() {
        return size;
  }

}
