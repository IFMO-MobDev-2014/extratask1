package com.example.android.picturemanager.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by lightning95 on 1/26/15.
 */

public class RestClient {
    private static final String CONSUMER_KEY = "x0ceDhj40VRAALAJRBjEqzjlDFdQzp0po0C4l6Xc";
    private static final String SERVICE_URL = "https://api.500px.com/v1";

    private ApiService apiService;

    public RestClient() {
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(SERVICE_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    public String getConsumerKey(){
        return CONSUMER_KEY;
    }
}
