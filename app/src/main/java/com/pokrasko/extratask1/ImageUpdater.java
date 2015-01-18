package com.pokrasko.extratask1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ImageUpdater extends IntentService {
    public static final String url = "http://api-fotki.yandex.ru/api/podhistory/?format=json";

    ResultReceiver receiver;
    int index;

    public static boolean running = false;

    public ImageUpdater() {
        super("Image Updater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        running = true;

        receiver = intent.getParcelableExtra("receiver");
        index = intent.getIntExtra("index", -1);

        if (index != -1) {
            try {
                receiver.send(ImageResultReceiver.PROGRESS, Bundle.EMPTY);
                Cursor cursor = getContentResolver().query(ImageContentProvider.CONTENT_IMAGES_URI,
                        null, "ind=" + index, null, null);
                cursor.moveToFirst();
                String link = cursor.getString(2);
                cursor.close();
                Bitmap image = BitmapFactory.decodeStream(new URL(link).openConnection()
                        .getInputStream());
                saveImage(image, index, true);
                receiver.send(ImageResultReceiver.OK, Bundle.EMPTY);
            } catch (Exception e) {
                receiver.send(ImageResultReceiver.ERROR, Bundle.EMPTY);
            }
        } else {
            try {
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new URL(url).openConnection().getInputStream()
                ));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                reader.close();
                JSONArray entries = new JSONObject(builder.toString()).getJSONArray("entries");

                getContentResolver().delete(ImageContentProvider.CONTENT_IMAGES_URI, null, null);
                JSONObject entry;
                for (int i = 0; i < MainActivity.AMOUNT; ++i) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("progress", i);
                    receiver.send(ImageResultReceiver.PROGRESS, bundle);

                    entry = entries.getJSONObject(i);
                    String pLink = entry.getJSONObject("img")
                            .getJSONObject(getBaseContext().getString(R.string.preview_mod))
                            .getString("href");
                    String fLink = entry.getJSONObject("img")
                            .getJSONObject(getBaseContext().getString(R.string.full_mod))
                            .getString("href");
                    String aLink = entry.getJSONObject("links").getString("alternate");
                    ContentValues values = new ContentValues();
                    values.put("ind", i);
                    values.put("hLink", fLink);
                    values.put("aLink", aLink);
                    getContentResolver().insert(ImageContentProvider.CONTENT_IMAGES_URI, values);

                    URL url = new URL(pLink);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    saveImage(image, i, false);
                }
                receiver.send(ImageResultReceiver.OK, Bundle.EMPTY);
            } catch (Exception e) {
                e.printStackTrace();
                receiver.send(ImageResultReceiver.ERROR, Bundle.EMPTY);
            }
        }
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    public void saveImage(Bitmap bitmap, int index, boolean isFull) {
        try {
            String size;
            if (isFull) {
                size = "full";
            } else {
                size = "preview";
            }
            deleteFile("preview" + index);
            deleteFile("full" + index);
            FileOutputStream fos = this.openFileOutput(size + index, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
