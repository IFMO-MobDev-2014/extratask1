package ru.ifmo.ctddev.soloveva.photoviewer;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ifmo.ctddev.soloveva.photoviewer.image.ImageDownloader;
import ru.ifmo.ctddev.soloveva.photoviewer.px500.PhotoInfo;
import ru.ifmo.ctddev.soloveva.photoviewer.util.ImageUtil;

/**
 * Created by maria on 17.01.15.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private final ImageDownloader downloader;
    private List<PhotoInfo> photos = Collections.emptyList();

    public PhotosAdapter(ImageDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell, parent, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        return new ViewHolder(view, imageView, progressBar);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.previousTask != null) {
            holder.previousTask.cancel(true);
        }
        PhotoInfo imageData = photos.get(position);
        String url = imageData.getImageUrls()[0];
        if (downloader.isCached(url)) {
            Bitmap bitmap = downloader.getFromCache(url);
            if (bitmap == null) {
                holder.imageView.setImageResource(R.drawable.not_available);
            } else {
                holder.imageView.setImageBitmap(bitmap);
            }
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            holder.imageView.setImageDrawable(ImageUtil.getSquareDummy());
            holder.previousTask = new ImageDownloadTask(holder.progressBar, holder.imageView);
            holder.previousTask.execute(url);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder.previousTask != null) {
            holder.previousTask.cancel(true);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<PhotoInfo> photos) {
        this.photos = new ArrayList<>(photos);
        notifyDataSetChanged();
    }

    public PhotoInfo getItem(int position) {
        return photos.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final ProgressBar progressBar;
        ImageDownloadTask previousTask;

        ViewHolder(View view, ImageView imageView, ProgressBar progressBar) {
            super(view);
            this.imageView = imageView;
            this.progressBar = progressBar;
        }
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private final ProgressBar progressBar;
        private final ImageView imageView;
        private IOException exception;

        private ImageDownloadTask(ProgressBar progressBar, ImageView imageView) {
            this.progressBar = progressBar;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            try {
                return downloader.download(url[0]);
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            if (bitmap == null) {
                Log.e("img", "Image download failed", exception);
                imageView.setImageResource(R.drawable.not_available);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
