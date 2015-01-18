package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ru.ifmo.md.extratask1.db.ImageContentProvider;
import ru.ifmo.md.extratask1.db.ImageDBHelper;

/**
 * Created by Mikhail on 18.01.15.
 */
public class ImageSearchAndDownloadService extends IntentService {
    private static final String url = "http://api-fotki.yandex.ru/api/podhistory/?format=json";
    private static final String smallSize = "M";
    private static final String fullSize = "XXL";

    public ImageSearchAndDownloadService() {
        super("ImageSearchAndDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        receiver.send(AppResultsReceiver.STATUS_RUNNING, Bundle.EMPTY);

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
            String smallSizeLink;
            String fullSizeLink;
            URL url;

            getContentResolver().delete(ImageContentProvider.LINK_CONTENT_URL, null ,null);
            ArrayList<Bitmap> downloadedImages = new ArrayList<>();
            for (int i = 0; i < 48; i++) {
                Bundle bundle = new Bundle();
                bundle.putInt("count", i+1);
                receiver.send(AppResultsReceiver.STATUS_RUNNING, bundle);

                entry = entries.getJSONObject(i);
                smallSizeLink = entry.getJSONObject("img").getJSONObject(smallSize).getString("href");
                fullSizeLink = entry.getJSONObject("img").getJSONObject(fullSize).getString("href");
                url = new URL(smallSizeLink);

                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                downloadedImages.add(i, image);

                ContentValues values = new ContentValues();
                values.put(ImageDBHelper.COLUMN_NAME_FULL_SIZE, "no");
                values.put(ImageDBHelper.COLUMN_NAME_MY_ID, i);
                values.put(ImageDBHelper.COLUMN_NAME_FULL_SIZE_LINK, fullSizeLink);
                if (i >= 0 && i <= 11) {
                    values.put(ImageDBHelper.COLUMN_NAME_PAGE, "1");
                } else if (i >= 12 && i <= 23) {
                    values.put(ImageDBHelper.COLUMN_NAME_PAGE, "2");
                } else if (i >= 24 && i <= 35) {
                    values.put(ImageDBHelper.COLUMN_NAME_PAGE, "3");
                } else {
                    values.put(ImageDBHelper.COLUMN_NAME_PAGE, "4");
                }
                getContentResolver().insert(ImageContentProvider.LINK_CONTENT_URL, values);
                saveImageToInternalStorage(image, i);
            }

            receiver.send(AppResultsReceiver.STATUS_FINISHED, Bundle.EMPTY);
        } catch (IOException e) {
            receiver.send(AppResultsReceiver.STATUS_INTERNET_ERROR, Bundle.EMPTY);
        } catch (JSONException e) {
            receiver.send(AppResultsReceiver.STATUS_PARSE_ERROR, Bundle.EMPTY);
            e.printStackTrace();
        }
    }

    FileOutputStream fos;
    public void saveImageToInternalStorage(Bitmap bitmap, int id) {
        try {
            fos = this.openFileOutput(Integer.toString(id), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
