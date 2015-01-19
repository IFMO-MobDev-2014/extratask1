package com.pokrasko.extratask1;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

public class PageFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    GridView gridView;

    public PageFragment() {
    }

    public static PageFragment newInstance(int sectionNumber) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int pos = getArguments().getInt(ARG_SECTION_NUMBER);
        View containerView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) containerView.findViewById(R.id.gridView);
        if (!MainActivity.images.isEmpty()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                List<Bitmap> screen = MainActivity.images.subList(10 * pos, 10 * (pos + 1));
                gridView.setAdapter(new GridAdapter(getActivity(), screen, 10 * pos));
            } else {
                List<Bitmap> screen = MainActivity.images.subList(12 * pos,
                        Math.min(100, 12 * (pos + 1)));
                gridView.setAdapter(new GridAdapter(getActivity(), screen, 12 * pos));
            }
        }
        return containerView;
    }
}
