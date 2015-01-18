package mariashka.editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariashka on 1/18/15.
 */
public class GridPhotoFragment extends Fragment{
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    List<PhotoItem> gridItems = new ArrayList<>();
    private Activity activity;
    int idx;

    public GridPhotoFragment(List<PhotoItem> gridItems, Activity activity, int idx) {
        this.gridItems = gridItems;
        this.activity = activity;
        this.idx = idx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_fragment, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (activity != null) {

            mGridAdapter = new GridAdapter(activity.getApplicationContext(), R.id.image, gridItems);
            if (mGridView != null) {
                mGridView.setAdapter(mGridAdapter);
            }

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view,
                                        int position, long id) {
                    ((Main)activity).onGridItemClick((GridView) parent, view, position + idx, id);
                }
            });
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
