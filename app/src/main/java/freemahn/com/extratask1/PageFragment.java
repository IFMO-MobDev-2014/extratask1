package freemahn.com.extratask1;

/**
 * Created by Freemahn on 18.01.2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class PageFragment extends Fragment {

    static String padeNumber = "page_number";
    int pageNumber;

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(padeNumber, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(padeNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid, null);
        GridView gvPage = (GridView) view.findViewById(R.id.myGridView);

        gvPage.setAdapter(new ImageAdapter(getActivity(),MainActivity.entries.subList(pageNumber * 9, pageNumber * 9 + 9)));

        return view;
    }
}