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
        super("ImageUpdater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        running = true;

        receiver = intent.getParcelableExtra("receiver");
        index = intent.getIntExtra("index", -1);
        boolean image = intent.getBooleanExtra("image", false);

        if (index != -1) {
            try {
                receiver.send(ImageResultReceiver.PROGRESS, Bundle.EMPTY);

                Cursor cursor = getContentResolver().query(ImageContentProvider.CONTENT_IMAGES_URI,
                        null, ImageContentProvider.INDEX_FIELD + "=" + index, null, null);
                cursor.moveToFirst();
                String title = cursor.getString(cursor.getColumnIndex(ImageContentProvider.TITLE_FIELD));
                String full = cursor.getString(cursor.getColumnIndex(ImageContentProvider.FULL_FIELD));
                String page = cursor.getString(cursor.getColumnIndex(ImageContentProvider.PAGE_FIELD));
                cursor.close();

                if (image) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(full).openConnection()
                            .getInputStream());
                    saveImage(bitmap, index, true);
                }

                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("full", full);
                bundle.putString("page", page);
                bundle.putBoolean("image", image);
                receiver.send(ImageResultReceiver.OK, bundle);
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

                for (int i = 0; i < MainActivity.AMOUNT; i++) {
                    deleteFile("full" + index);
                }

                for (int i = 0; i < MainActivity.AMOUNT; i++) {
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
                    String title = entry.getString("title");
                    String aLink = entry.getJSONObject("links").getString("alternate");
                    ContentValues values = new ContentValues();
                    values.put(ImageContentProvider.INDEX_FIELD, i);
                    values.put(ImageContentProvider.TITLE_FIELD, title);
                    values.put(ImageContentProvider.FULL_FIELD, fLink);
                    values.put(ImageContentProvider.PAGE_FIELD, aLink);
                    getContentResolver().insert(ImageContentProvider.CONTENT_IMAGES_URI, values);

                    URL url = new URL(pLink);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    saveImage(bitmap, i, false);
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
            deleteFile(size + index);
            FileOutputStream fos = this.openFileOutput(size + index, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
