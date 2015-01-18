package com.android.ilya.extratask1;

/**
 * Created by Ilya on 16.01.2015.
 */
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadImageTask extends IntentService {
    public static String serviceName = "downloadImageTask";
    public static final String ACTION_RESPONSE = "com.android.ilya.extratask1.downloadImageTask.RESPONSE";
    public static final int RESULT_ERROR = -100;
    public static final String TAG_PERCENT = "percent";

    public DownloadImageTask() {
        super(serviceName);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Intent response = new Intent();
        response.setAction(ACTION_RESPONSE);
        response.addCategory(Intent.CATEGORY_DEFAULT);
        response.putExtra(TAG_PERCENT, 0);
        sendBroadcast(response);

        try {
            getContentResolver().delete(
                    ImageContentProvider.IMAGES_URI,
                    null,
                    null
            );

            String url = "https://api.flickr.com/services/rest";
            JSONParser parser = new JSONParser();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("method", "flickr.photos.getRecent"));
            params.add(new BasicNameValuePair("api_key", "d9b7d0e96cb335ded4d607a74330bda5"));
            params.add(new BasicNameValuePair("format", "json"));
            params.add(new BasicNameValuePair("per_page", "30"));
            params.add(new BasicNameValuePair("media", "photos"));
            JSONObject b = parser.makeHttpRequest(url, params);
            JSONArray a = b.getJSONObject("photos").getJSONArray("photo");

            for (int i = 0; i < 24; i++) {
                JSONObject s = a.getJSONObject(i);
                String farmid = s.getString("farm");
                String serverid = s.getString("server");
                String id = s.getString("id");
                String secret = s.getString("secret");
                URL imgUrl = new URL("http://farm" + farmid + ".staticflickr.com/" + serverid + "/" + id + "_" + secret + "_m.jpg");

                Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                ContentValues cv = new ContentValues();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);

                byte[] bArray = bos.toByteArray();
                cv.put(MyDatabase.COLUMN_PICTURE, bArray);

                getContentResolver().insert(ImageContentProvider.IMAGES_URI, cv);

                Intent response2 = new Intent();
                response2.setAction(ACTION_RESPONSE);
                response2.addCategory(Intent.CATEGORY_DEFAULT);
                response2.putExtra(TAG_PERCENT, Math.round((float) (i + 1) / (float) 24 * (float) 100));
                sendBroadcast(response2);
            }

        } catch (Exception e) {
            Intent response2 = new Intent();
            response2.setAction(ACTION_RESPONSE);
            response2.addCategory(Intent.CATEGORY_DEFAULT);
            response2.putExtra(TAG_PERCENT, RESULT_ERROR);
            sendBroadcast(response2);
            e.printStackTrace();
        }
    }
}


