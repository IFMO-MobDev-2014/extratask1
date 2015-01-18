package ru.ifmo.md.extratask1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;


/**
 * Created by pinguinson on 16.01.2015.
 */
public class PageFragment extends Fragment {
    public static final int IMAGES_PER_PAGE = 6;
    ThumbnailAdapter adapter;
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
        adapter = new ThumbnailAdapter(gallery, getActivity());
        List<Photo> photos = ((MainActivity) getActivity()).allPhotos.subList(pageNumber * IMAGES_PER_PAGE, (pageNumber + 1) * IMAGES_PER_PAGE);
        adapter.setData(photos);
        gallery.setAdapter(adapter);

        return view;
    }
}
