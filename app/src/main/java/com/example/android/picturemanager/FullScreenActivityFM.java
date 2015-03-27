package com.example.android.picturemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.picturemanager.rest.model.Photo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class FullScreenActivityFM extends ActionBarActivity {
    private static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.fullscreen_background)
            .showImageOnFail(R.drawable.fullscreen_background)
            .resetViewBeforeLoading(false)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.NONE)
            .build();

    private static int position;
    private static boolean ifDownloaded = false;
    private static ArrayList<Photo> items;

    private CollectionPagerAdapter mCollectionPagerAdapter;
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_activity_fm);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        items = (ArrayList<Photo>) Photo.getPhotos(intent.getStringArrayListExtra("items"));

        mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager(), items);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.ic_withoutbackground);
        actionBar.setTitle(items.get(position).getName());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setTitle(items.get(position).getName());
                FullScreenActivityFM.position = position;
                ifDownloaded = false;
            }
        });

        mViewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_screen_activity_fm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_in_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.serviceURL)
                    + items.get(position).getBrowser_url()));
            startActivity(intent);
        }
        if (!ifDownloaded) {
            return super.onOptionsItemSelected(item);
        }

        if (id == R.id.action_set_as_wallpaper || id == R.id.action_download) {
            Photo photo = items.get(position);
            Intent intent = new Intent(this, LocalFeaturesService.class);
            intent.putExtra("title", photo.getTitle());
            intent.putExtra("big_image_url", photo.getBig_image_url());
            intent.setAction(id == R.id.action_set_as_wallpaper ?
                    getString(R.string.actionSetAsWallpaper) :
                    getString(R.string.actionDownload));
            startService(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class CollectionPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Photo> items;

        public CollectionPagerAdapter(FragmentManager fm, ArrayList<Photo> items) {
            super(fm);
            setItems(items);
        }

        public void setItems(ArrayList<Photo> items) {
            this.items = items;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new ObjectFragment();

            Bundle args = new Bundle();

            args.putString(ObjectFragment.NAME, items.get(i).getName());
            args.putString(ObjectFragment.USERNAME, items.get(i).getUsername());
            args.putString(ObjectFragment.BIG_IMAGE_URL, items.get(i).getBig_image_url());

            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    public static class ObjectFragment extends Fragment {
        private static String NAME = "name";
        private static String USERNAME = "username";
        private static String BIG_IMAGE_URL = "big_image_url";

        public static String HIDE_SYSTEM_UI = "hide_system_ui";

        private static final int SYSTEM_UI_INVISIBLE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        private static final int SYSTEM_UI_VISIBLE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        private ScaleImageView scaleImageView;
        private TextView textView;
        private ProgressBar progressBar;

        private String name;
        private String username;
        private String bigImageUrl;
        private IntentFilter hideFilter = new IntentFilter(HIDE_SYSTEM_UI);

        private BroadcastReceiver onHide = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (textView.getVisibility() == View.INVISIBLE) {
                    showSystemUI();
                } else {
                    hideSystemUI();
                }
            }
        };

        private static boolean isNetworkAvailable(Context context) {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        private void noInternetConnection() {
            Toast.makeText(getActivity(), getString(R.string.toastNoInternetConnection), Toast.LENGTH_SHORT).show();
        }

        private void hideSystemUI() {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }

        private void showSystemUI() {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle args = getArguments();
            name = args.getString(NAME);
            username = args.getString(USERNAME);
            bigImageUrl = args.getString(BIG_IMAGE_URL);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_full_screen_activity_fm, container, false);

            textView = (TextView) rootView.findViewById(R.id.textView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            scaleImageView = (ScaleImageView) rootView.findViewById(R.id.image);

            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();
            getActivity().unregisterReceiver(onHide);
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().registerReceiver(onHide, hideFilter);
        }

        private void displayImage() {
            ImageLoader.getInstance()
                    .displayImage(bigImageUrl, scaleImageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            if (failReason.getType() == FailReason.FailType.IO_ERROR) {
                                if (!isNetworkAvailable(getActivity())) {
                                    noInternetConnection();
                                }
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ifDownloaded = true;
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    });
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            textView.setVisibility(View.INVISIBLE);
            textView.setText(name + "\n by " + username);

            getActivity().registerReceiver(onHide, hideFilter);
            hideSystemUI();

            if (bigImageUrl == null) {
                Log.d("FSA", "big_image_url == null");
            } else {
                displayImage();
            }
        }
    }
}
