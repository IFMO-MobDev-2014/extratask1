package com.example.vitalii.extra;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Vitalii on 18.01.2015.
 */
public class MyApplication extends Application {
    public static boolean isPopularLoaded = false;
    public static boolean isDayPhotoLoaded = false;
    @Override
    public void onCreate() {
        super.onCreate();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(android.R.drawable.alert_dark_frame)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
