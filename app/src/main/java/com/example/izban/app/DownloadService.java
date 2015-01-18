package com.example.izban.app;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by izban on 17.01.15.
 */
public class DownloadService extends IntentService {
    public static final int PICTURES = 30;

    public DownloadService() {
        super(DownloadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("", "start DownloadService");
            final ResultReceiver receiver = intent.getParcelableExtra(Constants.RECEIVER);
            receiver.send(Constants.RECEIVER_STARTED, Bundle.EMPTY);

            URL url = new URL("http://api-fotki.yandex.ru/api/podhistory/?format=json");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            InputStream is = connect.getInputStream();
            Scanner in = new Scanner(is);
            String s = "";
            while (in.hasNext()) {
                s += in.next();
            }
            JSONArray jsons = new JSONObject(s).getJSONArray("entries");

            ContentValues cv[] = new ContentValues[PICTURES];
            for (int i = 0; i < PICTURES; i++) {
                String filepath = "" + i;
                JSONObject item = jsons.getJSONObject(i);
                cv[i] = new ContentValues();
                cv[i].put(DatabaseHelper.IMAGES_LINK, item.getJSONObject("img").getJSONObject("XXL").getString("href"));
                cv[i].put(DatabaseHelper.IMAGES_FILEPATH, filepath);

                try {
                    url = new URL(item.getJSONObject("img").getJSONObject("M").getString("href"));
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    FileOutputStream outputStream = this.openFileOutput(filepath, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (IOException e) {
                    Log.i("", String.format("downloading picture %d failed", i + 1));
                }

                receiver.send(Constants.RECEIVER_RUNNING, Bundle.EMPTY);
            }
            Uri uri = Uri.parse("content://" + MyContentProvider.AUTHORITY + "/" + DatabaseHelper.IMAGES_TABLE_NAME);
            getContentResolver().delete(uri, null, null);
            getContentResolver().bulkInsert(uri, cv);
            receiver.send(Constants.RECEIVER_FINISHED, Bundle.EMPTY);
            Log.i("", "service ok");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.i("", "service failed");
        }
    }
}
