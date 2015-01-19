package ru.ifmo.instafeed.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by creed on 17.01.15.
 */
public class RestClient {
    public static final String BASE_URL = "https://api.instagram.com/v1";
    public static final String ACCESS_TOKEN = "326054308.1fb234f.f400fdc6212b4d05947dde9ba7f2f8f9";
    private ApiService apiService;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
            .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
            .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(BASE_URL)
            .setConverter(new GsonConverter(gson))
            .build();

        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService()
    {
        return apiService;
    }
}
