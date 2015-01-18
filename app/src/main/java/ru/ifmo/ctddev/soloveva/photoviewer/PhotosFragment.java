package ru.ifmo.ctddev.soloveva.photoviewer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import ru.ifmo.ctddev.soloveva.photoviewer.image.ImageDownloader;
import ru.ifmo.ctddev.soloveva.photoviewer.px500.PhotoInfo;
import ru.ifmo.ctddev.soloveva.photoviewer.px500.PhotoList;
import ru.ifmo.ctddev.soloveva.photoviewer.px500.Px500Api;
import ru.ifmo.ctddev.soloveva.photoviewer.util.ImageUtil;
import ru.ifmo.ctddev.soloveva.photoviewer.util.RecyclerItemClickListener;

/**
 * Created by maria on 17.01.15.
 */
public class PhotosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerItemClickListener.OnItemClickListener {
    public static final String CATEGORY_KEY = "category";
    public static final String PAGE_KEY = "page";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PhotosAdapter photosAdapter;

    private String category;
    private int page;
    private GetListTask getListTask;
    private ImageDownloader imageDownloader;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        category = bundle.getString(CATEGORY_KEY);
        page = bundle.getInt(PAGE_KEY);

        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        swipeRefreshLayout.setOnRefreshListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        recyclerView.setHasFixedSize(true);

        imageDownloader = new ImageDownloader();
        photosAdapter = new PhotosAdapter(imageDownloader);
        recyclerView.setAdapter(photosAdapter);

        getListTask = new GetListTask();
        getListTask.execute();

        return view;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (getListTask != null) {
            getListTask.cancel(true);
        }
        getListTask = new GetListTask();
        getListTask.execute();
    }

    @Override
    public void onDestroyView() {
        getListTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public void onItemClick(View view, int position) {
        PhotoInfo item = photosAdapter.getItem(position);

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.cell, null);

        ImageView imageView = (ImageView) contentView.findViewById(R.id.image_view);
        ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.progress_bar);
        String url = item.getImageUrls()[1];
        final ShowFullscreenTask showFullscreenTask;
        if (imageDownloader.isCached(url)) {
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Bitmap bitmap = imageDownloader.getFromCache(url);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.not_available);
            }
            showFullscreenTask = null;
        } else {
            imageView.setImageDrawable(ImageUtil.createDummyDrawable(item.getWidth(), item.getHeight()));
            showFullscreenTask = new ShowFullscreenTask(imageView, progressBar);
            showFullscreenTask.execute(url);
        }

        dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x77000000));
        dialog.setContentView(contentView);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (showFullscreenTask != null) {
                    showFullscreenTask.cancel(true);
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    private class GetListTask extends AsyncTask<Void, Void, PhotoList> {
        @Override
        protected PhotoList doInBackground(Void... params) {
            Px500Api api = new Px500Api();
            try {
                return api.getPhotoList(category, page);
            } catch (IOException e) {
                Log.w("api", e);
                return null;
            } finally {
                try {
                    api.close();
                } catch (IOException ignored) {}
            }
        }

        @Override
        protected void onPostExecute(PhotoList photoList) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (photoList == null) {
                Toast.makeText(getActivity(), R.string.inet_error, Toast.LENGTH_LONG).show();
            } else {
                photosAdapter.setPhotos(photoList.getPhotos());
            }
        }
    }

    private class ShowFullscreenTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;
        private final ProgressBar progressBar;

        private ShowFullscreenTask(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                return imageDownloader.download(params[0]);
            } catch (IOException e) {
                Log.w("full", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(View.GONE);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.not_available);
            }
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
