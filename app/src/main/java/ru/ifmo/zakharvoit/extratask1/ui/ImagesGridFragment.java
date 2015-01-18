package ru.ifmo.zakharvoit.extratask1.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ru.ifmo.zakharvoit.extratask1.R;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureColumns;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureCursor;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureSelection;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class ImagesGridFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        GridView.OnItemClickListener {

    public static Fragment newInstance(int from, int to) {
        ImagesGridFragment fragment = new ImagesGridFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(FROM_ARGUMENT, from);
        arguments.putInt(TO_ARGUMENT, to);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static final String FROM_ARGUMENT = "from";
    public static final String TO_ARGUMENT = "to";

    private ImagesAdapter adapter;
    private String[] selectionArgs;
    private String selection;
    private int loadersCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int from = arguments.getInt(FROM_ARGUMENT);
        int to = arguments.getInt(TO_ARGUMENT);

        View rootView = getLayoutInflater(savedInstanceState)
                .inflate(R.layout.fragment_images_grid, container, false);

        selection = PictureColumns.MY_ID + " >= "
                + from + " and "
                + PictureColumns.MY_ID + " < "
                + to;
        selectionArgs = new String[] {};

        PictureSelection pictureSelection = new PictureSelection();
        pictureSelection.addRaw(selection, selectionArgs);
        PictureCursor cursor = pictureSelection.query(getActivity().getContentResolver());

        Log.d("CURSOR_SIZE_SUKA", cursor.getCount() + "");

        adapter = new ImagesAdapter(getActivity(), cursor);

        GridView imagesGrid = (GridView) rootView.findViewById(R.id.images_grid);
        imagesGrid.setAdapter(adapter);
        imagesGrid.setOnItemClickListener(this);

        getLoaderManager().initLoader(loadersCount++, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("LoaderCallbacks", "Create loader");

        return new CursorLoader(getActivity(), PictureColumns.CONTENT_URI,
                null,
                selection,
                selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("LoaderCallbacks", "Load finished " + data.getCount());
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("LoaderCallbacks", "Load reset");
        adapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("MainActivity.onItemClick", "Clicked " + id);

        PictureCursor cursor = new PictureSelection()
                .id(id).query(getActivity().getContentResolver());
        cursor.moveToFirst();

        String title = cursor.getTitle();
        String link = cursor.getLargeLink();

        Intent intent = new Intent(getActivity(), FullImageActivity.class);
        intent.putExtra(FullImageActivity.IMAGE_TITLE_EXTRA, title);
        intent.putExtra(FullImageActivity.IMAGE_LINK_EXTRA, link);
        startActivity(intent);
    }
}
