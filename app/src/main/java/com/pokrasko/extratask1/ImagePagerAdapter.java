package com.pokrasko.extratask1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImagePagerAdapter extends FragmentPagerAdapter {
    int widthNumber;
    int heightNumber;

    public ImagePagerAdapter(FragmentManager fm, int widthNumber, int heightNumber) {
        super(fm);
        this.widthNumber = widthNumber;
        this.heightNumber = heightNumber;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return ((Double) Math.ceil((double) MainActivity.AMOUNT / (widthNumber * heightNumber)))
                .intValue();
    }
}
