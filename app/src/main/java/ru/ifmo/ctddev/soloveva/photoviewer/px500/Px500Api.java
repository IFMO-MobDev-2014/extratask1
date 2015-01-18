package ru.ifmo.ctddev.soloveva.photoviewer.px500;

import android.net.Uri;
import android.net.http.AndroidHttpClient;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by maria on 17.01.15.
 */
public class Px500Api implements Closeable {
    private static final String PHOTO_LIST_URL = "https://api.500px.com/v1/photos";
    private static final String CONSUMER_KEY = "TXvOarjNewNE2AcgtulmgjaTuzfTe656sSWsC6oI";

    private Gson gson = new Gson();
    private AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");

    public PhotoList getPhotoList(String feature, int page) throws IOException {
        Uri uri = Uri.parse(PHOTO_LIST_URL)
                        .buildUpon()
                        .appendQueryParameter("feature", feature)
                        .appendQueryParameter("consumer_key", CONSUMER_KEY)
                        .appendQueryParameter("page", Integer.toString(page))
                        .appendQueryParameter("image_size[]", "3")
                        .appendQueryParameter("image_size[]", "4")
                        .build();
        HttpResponse response = httpClient.execute(new HttpGet(URI.create(uri.toString())));
        return gson.fromJson(new InputStreamReader(response.getEntity().getContent()), PhotoList.class);
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
