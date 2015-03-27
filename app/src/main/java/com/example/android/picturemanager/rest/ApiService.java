package com.example.android.picturemanager.rest;

import com.example.android.picturemanager.rest.model.Response;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by lightning95 on 1/26/15.
 */

public interface ApiService {
    @GET("/photos")
    public void getFeed(@Query("feature") String category,
                        @Query("image_size") int imageSize,
                        @Query("page") int pageNumber,
                        @Query("rpp") int imagesPerPage,
                        @Query("consumer_key") String consumerKey,
                        Callback<Response> callback);
}

