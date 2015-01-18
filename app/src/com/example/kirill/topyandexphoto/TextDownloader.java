package com.example.kirill.topyandexphoto;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Kirill on 11.01.2015.
 */
public class TextDownloader {

    public TextDownloader() {}

    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String load(String urlString, Map<String, String> requestProperties) {
        InputStream is = null;
        String rv = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            for (Map.Entry<String, String> i : requestProperties.entrySet()) {
                urlConnection.setRequestProperty(i.getKey(), i.getValue());
            }
            is = urlConnection.getInputStream();
            rv = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rv;
    }

}
