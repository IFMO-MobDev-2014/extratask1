package com.example.android.picturemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.picturemanager.model.MyFeed;
import com.example.android.picturemanager.rest.model.Photo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by lightning95 on 1/27/15.
 */

public class ImageAdapter extends BaseAdapter {
    private static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grid)
            .showImageOnFail(R.drawable.grid)
            .resetViewBeforeLoading(false)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();

    private Context context;
    private List<Photo> items;
    private String category;

    public ImageAdapter(Context context, String category) {
        this.context = context;
        this.category = category;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Photo> items) {
        this.items = items;
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void noInternetConnection() {
        Toast.makeText(context, context.getString(R.string.toastNoInternetConnection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        Photo currentItem = items.get(position);

        ImageLoader.getInstance().displayImage(currentItem.getImage_url(), imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (!isNetworkAvailable()) {
                    noInternetConnection();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Intent progress = new Intent();
                progress.setAction(MyFeed.PROGRESS_BROADCAST);
                progress.putExtra("category", category);
                context.sendBroadcast(progress);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        return imageView;
    }
}
