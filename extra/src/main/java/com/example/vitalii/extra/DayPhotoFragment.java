package com.example.vitalii.extra;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DayPhotoFragment extends Fragment {
    private static final String TAG = PopularPhotoFragment.class.getName();
    private RecyclerView recyclerView;
    private RecycleViewAdapter recycleViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private  RecycleViewAdapter adapter;
    private Gallery my_gallery;

    public void Update() {
        adapter = new RecycleViewAdapter();
        RestAdapter restAdapter = new RestAdapter.Builder().
                setEndpoint("http://api-fotki.yandex.ru").build();
        PhotoAPI photoAPI = restAdapter.create(PhotoAPI.class);
        photoAPI.getPhoto("podhistory", new Callback<Gallery>() {
            @Override
            public void success(Gallery gallery, Response response) {
                my_gallery = gallery;
                Log.d(TAG, "Gallery downloaded");
                MyApplication.isDayPhotoLoaded = true;
                for (int i = 0; i < 50; i++) {
                    adapter.add(my_gallery.entries.get(i).img.XL.href, i);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Gallery download fail" + error.getMessage());
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_photo, null);
        if (!MyApplication.isDayPhotoLoaded) {
            Update();
        }
        setHasOptionsMenu(true);
        for (int i = 0; i < 50; i++) {
            if (my_gallery != null)
                adapter.add(my_gallery.entries.get(i).img.XL.href, i);
        }
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.day_photo_view);
        layoutManager = new GridLayoutManager(getActivity().getBaseContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
        recyclerView.setItemAnimator(itemAnimator);
    }
}
