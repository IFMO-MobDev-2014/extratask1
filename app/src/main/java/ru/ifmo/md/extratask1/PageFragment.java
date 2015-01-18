package ru.ifmo.md.extratask1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;
import java.util.Random;

public class PageFragment extends Fragment {
    ImageAdapter adapter;
    private int backColor;
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

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, null);

        GridView gallery = (GridView) view.findViewById(R.id.gallery);
        adapter = new ImageAdapter(gallery, getActivity());
        List<String> pageUrls = ((ResultsList) getActivity()).allUrls.subList(pageNumber * 10, (pageNumber + 1) * 10);
        adapter.setData(pageUrls);
        gallery.setAdapter(adapter);
        gallery.setBackgroundColor(backColor);

        return view;
    }
}
