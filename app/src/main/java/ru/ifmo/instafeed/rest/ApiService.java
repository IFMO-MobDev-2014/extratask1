package ru.ifmo.instafeed.rest;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.ifmo.instafeed.rest.model.Response;

/**
 * Created by creed on 17.01.15.
 */
public interface ApiService {
    @GET("/users/self/feed")
    public void usersSelfFeed(@Query("access_token") String accessToken,
                              @Query("max_id") String maxId,
                              @Query("count") Integer count,
                              Callback<Response> callback);

    @GET("/media/popular")
    public void mediaPopular(@Query("access_token") String accessToken,
                             Callback<Response> callback);

    @GET("/media/search")
    public void mediaSearch(@Query("access_token") String accessToken,
                            @Query("lat") Float lat,
                            @Query("lng") Float lng,
                            @Query("distance") Integer distance,
                            Callback<Response> callback);
}
