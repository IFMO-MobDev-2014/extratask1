package com.example.izban.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by izban on 17.01.15.
 */
public class MainFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    ImagesAdapter adapter;
    int page;

    static MainFragment newInstance(int page) {
        MainFragment pageFragment = new MainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("page", page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        page = getArguments().getInt("page");
        View rootView;
        GridView gridView;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = inflater.inflate(R.layout.fragment_main_landscape, container, false);
            gridView = ((GridView)rootView.findViewById(R.id.gridView2));
        } else {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            gridView = ((GridView)rootView.findViewById(R.id.gridView));
        }
        adapter = new ImagesAdapter(getActivity(), android.R.layout.simple_list_item_1);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("link", adapter.getItem(i).link);
                intent.putExtra("title", adapter.getItem(i).title);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this).forceLoad();

        return rootView;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri base = Uri.parse("content://" + MyContentProvider.AUTHORITY);
        Uri uri = Uri.withAppendedPath(base, DatabaseHelper.IMAGES_TABLE_NAME);
        int l = page * Constants.ON_PAGE;
        int r = (page + 1) * Constants.ON_PAGE;
        String selection = DatabaseHelper.IMAGES_IND + " >= \"" + l + "\"" + " and " +
                DatabaseHelper.IMAGES_IND + " < \"" + r + "\"";
        Log.i("", selection);
        return new CursorLoader(getActivity(), uri, null, selection, null, null);
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
