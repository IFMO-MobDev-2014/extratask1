package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class UpdaterService extends IntentService {
    private static final String url = "http://api-fotki.yandex.ru/api/podhistory/?format=json";
    public UpdaterService() {
        super("UpdaterService");
    }
    ResultReceiver receiver;
    @Override
    protected void onHandleIntent(Intent intent) {


        receiver = intent.getParcelableExtra("receiver");
        try {
            StringBuilder builder = new StringBuilder();
            URLConnection connection = new URL(url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray entries = jsonObject.getJSONArray("entries");


            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                String link = entry.getJSONObject("img").getJSONObject("L").getString("href");

                // load into cache
                //Log.d("updater", link);
                if(GridActivity.cache.get(link) == null) {
                    // cant download
                    throw new Exception(link);
                }

                Bundle bundle = new Bundle();
                bundle.putString("url", link);
                receiver.send(ProgressReceiver.IMGLOADED, bundle);
            }
            receiver.send(ProgressReceiver.DONE, Bundle.EMPTY);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(ProgressReceiver.ERROR, Bundle.EMPTY);
        }
    }

}
