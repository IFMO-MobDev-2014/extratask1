package com.example.vitalii.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.Inflater;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    ArrayList<String> mData;
    private static final String TAG = RecycleViewAdapter.class.getName();
    private LayoutInflater inflator;
    private ProgressBar progressBar;

    public RecycleViewAdapter(ArrayList<String> mData) {

        this.mData = mData;
    }

    public RecycleViewAdapter() {
        super();
        mData = new ArrayList<>(100);
    }

    public void add(String item, int position) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void clear() {
        mData.clear();
        mData = new ArrayList<>(100);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = inflator.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item, viewGroup, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progresBar);
        progressBar.setVisibility(View.GONE);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String item = mData.get(i);
        //ImageLoader.getInstance().displayImage(item, viewHolder.icon);
        display(viewHolder.icon, item, progressBar);
    }

    public void display(ImageView img, String url, final ProgressBar spinner)
    {
        ImageLoader.getInstance().displayImage(url, img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE); // set the spinner visible
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE); // set the spinenr visibility to gone


            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE); //  loading completed set the spinenr visibility to gone
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }

        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick " + getPosition() + " " + "adasd");
            Intent intent = new Intent(v.getContext(), FullScreenActivity.class);
            intent.putExtra("href", mData.get(getPosition()));
            v.getContext().startActivity(intent);
        }
    }
}
