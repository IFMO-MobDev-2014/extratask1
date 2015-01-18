package com.example.vitalii.extra;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Vitalii on 18.01.2015.
 */
public interface PhotoAPI {
@GET("/api/{collection}/?limit=100;format=json")
    void getPhoto(@Path("collection") String str, Callback<Gallery> cb);
}


