package com.example.izban.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by izban on 17.01.15.
 */
public class MainFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    ImagesAdapter adapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new ImagesAdapter(getActivity(), android.R.layout.simple_list_item_1);
        ((GridView)rootView.findViewById(R.id.gridView)).setAdapter(adapter);
        for (int it = 0; it < 5; it++) {
            adapter.add(new MyImage());
        }
        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri base = Uri.parse("content://" + MyContentProvider.AUTHORITY);
        Uri uri = Uri.withAppendedPath(base, DatabaseHelper.IMAGES_TABLE_NAME);
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        if (adapter == null) {
            adapter = new ImagesAdapter(getActivity(), android.R.layout.simple_list_item_1);
        }
        adapter.clear();
        while (cursor.moveToNext()) {
            adapter.add(DatabaseHelper.getImage(cursor));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        adapter = null;
    }
}
