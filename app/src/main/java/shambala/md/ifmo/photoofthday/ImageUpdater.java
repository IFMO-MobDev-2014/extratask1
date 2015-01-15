package shambala.md.ifmo.photoofthday;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by 107476 on 10.01.2015.
 */
public class ImageUpdater extends IntentService {
    public static final String url = "http://api-fotki.yandex.ru/api/podhistory/?format=json";
    public  static final String thumbnail_size = "M";
    public  static final String fullscreen_size = "XXL";
    public ImageUpdater() {
        super("ImageUpdater");
    }
    ResultReceiver receiver;
    int index;
    @Override
    protected void onHandleIntent(Intent intent) {


        receiver = intent.getParcelableExtra("receiver");
        index = intent.getIntExtra("index", -1);
        if (index != -1) {
            try {
                receiver.send(ImagesReceiver.PROGRESS, Bundle.EMPTY);
                Cursor cursor = getContentResolver().query(ImagesContentProvider.LINKS_CONTENT_URI, null, "ind = ?", new String[]{""+index},null);
                cursor.moveToFirst();
                String link = cursor.getString(2);
                cursor.close();
                Bitmap image = BitmapFactory.decodeStream(new URL(link).openConnection().getInputStream());
                saveImage(image, 100 + index);
                receiver.send(ImagesReceiver.OK, Bundle.EMPTY);
            } catch (Exception e) {
                receiver.send(ImagesReceiver.ERROR, Bundle.EMPTY);
            }
            return;
        }

        try {
            StringBuilder builder = new StringBuilder();
            URLConnection connection = new URL(url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();
            String resultJson = builder.toString();
            JSONObject jsonObject = new JSONObject(resultJson);
            JSONArray entries = jsonObject.getJSONArray("entries");

            JSONObject entry;
            String link;
            String hLink;
            String onSiteLink;
            URL url;
            getContentResolver().delete(ImagesContentProvider.LINKS_CONTENT_URI, null ,null);
            ArrayList<Bitmap> newImages = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                Bundle bundle = new Bundle();
                bundle.putInt("progress", i+1);
                receiver.send(ImagesReceiver.PROGRESS, bundle);
                entry = entries.getJSONObject(i);
                link = entry.getJSONObject("img").getJSONObject(thumbnail_size).getString("href");
                hLink = entry.getJSONObject("img").getJSONObject(fullscreen_size).getString("href");
                onSiteLink = entry.getJSONObject("links").getString("alternate");
                url = new URL(link);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                newImages.add(i, image);
                ContentValues values = new ContentValues();
                values.put("ind", i);
                values.put("link", hLink);
                values.put("onsite", onSiteLink);
                getContentResolver().insert(ImagesContentProvider.LINKS_CONTENT_URI, values);
                saveImage(image, i);

            }
            MainActivity.downloaded = newImages;
            receiver.send(ImagesReceiver.OK, Bundle.EMPTY);
        } catch (Exception e) {
            receiver.send(ImagesReceiver.ERROR, Bundle.EMPTY);
        }
    }
    FileOutputStream fos;
    public void saveImage(Bitmap bitmap, int id) {

        try {
            fos = this.openFileOutput(""+id, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            Log.d("err", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("err", "io exception");
            e.printStackTrace();
        }

    }
}
