package ru.ifmo.instafeed.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import ru.ifmo.instafeed.ui.feed.ImageAdapter;
import ru.ifmo.instafeed.MainActivity;
import ru.ifmo.instafeed.rest.RestClient;
import ru.ifmo.instafeed.rest.model.Item;
import ru.ifmo.instafeed.rest.model.Response;

/**
 * Created by creed on 18.01.15.
 */
public class MyFeed {
    private List<Item> items;
    private String maxId;
    private RestClient restClient;
    private ImageAdapter imageAdapter;

    public MyFeed(RestClient restClient, ImageAdapter imageAdapter) {
        this.maxId = null;
        this.restClient = restClient;
        if (this.items == null) {
            this.items = new ArrayList<Item>();
        }
        this.imageAdapter = imageAdapter;
        this.imageAdapter.setItems(this.items);
    }

    //Getters

    public List<Item> getItems() {
        return items;
    }

    public String getMaxId() {
        return maxId;
    }

    //Setters

    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    //Other

    public void loadItems(String source) {
        if (source.equals("feed")) {
            restClient.getApiService().usersSelfFeed(RestClient.ACCESS_TOKEN, getMaxId(), 18, new Callback<Response>() {
                @Override
                public void success(Response response, retrofit.client.Response httpResponse) {
                    List<Item> received = response.getData();
                    ArrayList<Item> filtered = new ArrayList<Item>();
                    for(int i = 0; i < received.size(); i++) {
                        if (received.get(i).getType().equals("image")) {
                            filtered.add(received.get(i));
                        }
                    }

                    if (response.getPagination().getNextMaxId() == null) {
                        Log.e(MainActivity.TAG, response.toString());
                        //return;
                    }

                    setMaxId(response.getPagination().getNextMaxId());
                    appendItems(filtered);

                    if (!check()) {
                        Log.e(MainActivity.TAG, "Duplicating images");
                    }

                    Log.d(MainActivity.TAG, "/users/self/feed loaded. Total " + items.size() + " items");
                    Log.d(MainActivity.TAG, "next_max_id = " + getMaxId());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(MainActivity.TAG, "Error in REST Client");
                    Log.e(MainActivity.TAG, "MESSAGE: "+error.getMessage());
                }
            });
        } else if (source.equals("popular")) {
            restClient.getApiService().mediaPopular(RestClient.ACCESS_TOKEN, new Callback<Response>() {
                @Override
                public void success(Response response, retrofit.client.Response httpResponse) {
                    List<Item> received = response.getData();
                    ArrayList<Item> filtered = new ArrayList<Item>();
                    for(int i = 0; i < received.size(); i++) {
                        if (received.get(i).getType().equals("image")) {
                            filtered.add(received.get(i));
                        }
                    }


                    appendItems(filtered);

                    if (!check()) {
                        Log.e(MainActivity.TAG, "Duplicating images");
                    }

                    Log.d(MainActivity.TAG, "/media/popular loaded. Total " + items.size() + " items");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(MainActivity.TAG, "Error in REST Client");
                    Log.e(MainActivity.TAG, "MESSAGE: "+error.getMessage());
                }
            });
        }

    }

    public void appendItems(List<Item> other) {
        for(int i = 0; i < other.size(); i++) {
            items.add(other.get(i));
        }
        this.imageAdapter.notifyDataSetChanged();
    }

    /*public void clear() {
        this.maxId = null;
        this.items.clear();
        //this.imageAdapter.notifyDataSetChanged();
    }*/

    /*public void refresh() {
        this.clear();
        this.loadItems();
    }*/

    public Boolean check() {
        for(int i = 0; i < items.size(); i++) {
            for(int j = i + 1; j < items.size(); j++) {
                if (items.get(i).getLink().equals(items.get(j).getLink())) {
                    return false;
                }
            }
        }
        return true;
    }


}
