package com.example.izban.app;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by izban on 17.01.15.
 */
public class DownloadService extends IntentService {
    public DownloadService() {
        super(DownloadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            URL url = new URL("http://api-fotki.yandex.ru/api/podhistory/?format=json");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            InputStream is = connect.getInputStream();
            Scanner in = new Scanner(is);
            String s = "";
            while (in.hasNext()) {
                s += in.next();
            }
            JSONObject json = new JSONObject(s);

            Log.i("", " " + json.getJSONArray("entries").length());
            //Uri uri = Uri.parse("content://" + MyContentProvider.AUTHORITY + "/" + DatabaseHelper.IMAGES_TABLE_NAME);
            //getContentResolver().delete(uri, DatabaseHelper.ITEMS_CITY + " = \"" + city + "\"", null);
            //getContentResolver().bulkInsert(uri, cv);
            Log.i("", "service ok");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.i("", "service failed");
        }
    }
}
