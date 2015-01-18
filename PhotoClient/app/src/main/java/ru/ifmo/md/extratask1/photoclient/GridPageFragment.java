package ru.ifmo.md.extratask1.photoclient;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ru.ifmo.md.extratask1.photoclient.database.ImagesProvider;
import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;

/**
 * Created by sergey on 18.01.15.
 */
public class GridPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView mGridView;
    private GridImageAdapter mAdapter;
    private int mStartIndex;
    private int mLastIndex;

    public static final String EXTRA_START_INDEX = "start_index";
    public static final String EXTRA_LAST_INDEX = "last_index";


    public GridPageFragment() {
        //Empty constructor
    }

    public static GridPageFragment newInstance(int startIndex, int lastIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_START_INDEX, startIndex);
        bundle.putInt(EXTRA_LAST_INDEX, lastIndex);
        GridPageFragment fragment = new GridPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mStartIndex = arguments.getInt(EXTRA_START_INDEX);
        mLastIndex = arguments.getInt(EXTRA_LAST_INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_page_fragment, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.grid_view);
        mAdapter = new GridImageAdapter(getActivity(), null, false);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int rowId = cursor.getInt(cursor.getColumnIndex(ImagesTable.COLUMN_ID));
                cursor.close();
                Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.EXTRA_ROW_ID, rowId);
                startActivity(intent);
            }
        });

        getLoaderManager().restartLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (savedInstanceState != null) {
//            mIsRefreshing = savedInstanceState.getBoolean(FRAGMENT_STATE_REFRESHING);
//            mRefreshLayout.setRefreshing(mIsRefreshing);
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String whereClause = ImagesTable.COLUMN_ID + " >= " + mStartIndex + " AND " +
                ImagesTable.COLUMN_ID + " < " + mLastIndex;
        return new CursorLoader(getActivity(), ImagesProvider.CONTENT_URI, null, whereClause, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

}
