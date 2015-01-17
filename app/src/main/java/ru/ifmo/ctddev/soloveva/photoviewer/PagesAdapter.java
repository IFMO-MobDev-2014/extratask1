package ru.ifmo.ctddev.soloveva.photoviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by maria on 17.01.15.
 */
public class PagesAdapter extends FragmentStatePagerAdapter {
    private final String category;
    private final String titleFormat;
    private int pages;

    public PagesAdapter(FragmentManager fm, String category, String titleFormat, int pages) {
        super(fm);
        this.category = category;
        this.pages = pages;
        this.titleFormat = titleFormat;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new PhotosFragment();
        Bundle arguments = new Bundle();
        arguments.putString(PhotosFragment.CATEGORY_KEY, category);
        arguments.putInt(PhotosFragment.PAGE_KEY, i + 1);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getCount() {
        return pages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(titleFormat, position + 1);
    }
}
