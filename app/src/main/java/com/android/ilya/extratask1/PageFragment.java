package com.android.ilya.extratask1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


/**
 * Created by Ilya on 18.01.2015.
 */
import android.support.v4.app.Fragment;

public class PageFragment extends Fragment {
    ImageAdapter adapter;
    private int pageNumber;

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("page_number", page);
        pageFragment.setArguments(arguments);

        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt("page_number");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, null);

        GridView gallery = (GridView) view.findViewById(R.id.gallery);
            adapter = new ImageAdapter(getActivity(), ((MainActivity) getActivity()).images.subList(pageNumber * 6, (pageNumber + 1) * 6));
            gallery.setAdapter(adapter);
        return view;
    }
}
