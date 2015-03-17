package com.example.alexey.extratask1;

/**
 * Created by Alexey on 01.12.2014.
 */

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class IServise extends IntentService {
    public static final int IMGCOUNT = 24;
    static String RECEIVER = "1";
    static int STATUS_RUNNING = 2;
    static String RECEIVER_DATA = "4";
    static int STATUS_FINISHED = 5;

    public IServise() {
        this("IServise");
    }

    public IServise(String name) {
        super(name);
    }


    public void onCreate() {
        super.onCreate();

        Log.i("Started", "IService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("started", "onhandle");
        final Bundle data = new Bundle();
        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        data.putString(RECEIVER_DATA, "Sample result data");
        receiver.send(STATUS_RUNNING, data);
        String pictures;
        try {
            HttpClient client = new DefaultHttpClient();
            Uri.Builder uriBuilder = new Uri.Builder();
            //String path= (String) intent.getSerializableExtra("word");
            uriBuilder.scheme("https")
                    .authority("api.flickr.com")
                    .appendPath("services")
                    .appendPath("rest")
                    .appendPath("")
                    .appendQueryParameter("api_key", "4a32b392b522081a39ddd65c07e4bb18")
                    .appendQueryParameter("method", "flickr.interestingness.getList")
                    .appendQueryParameter("format", "json");
                    //.appendQueryParameter("text", path);
            HttpGet POST = new HttpGet(uriBuilder.build().toString());
            ResponseHandler<String> handler = new BasicResponseHandler();
            String response = client.execute(POST, handler);
            JSONObject jsonResponse = new JSONObject(response.substring(response.indexOf("(") + 1,
                    response.lastIndexOf(")")));
            JSONArray results = jsonResponse.getJSONObject("photos").getJSONArray("photo");
            int count = IMGCOUNT;
            if (count > results.length())
                count = results.length();
            int status;
            for (int i = 0; i < count; i++) {
                status = 5;
                if (i==0) status = 6;
                JSONObject result = results.getJSONObject(i);
                String farm = result.getString("farm");
                String server = result.getString("server");
                String id = result.getString("id");
                String secret = result.getString("secret");
                pictures = "https://farm" + farm + ".staticflickr.com/" +
                        server + "/" + id + "_" + secret + "_q.jpg";
             //   BoxAdapter  boxAdapter = new BoxAdapter(this, );
                Bitmap bitmap = ground(pictures);
                int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                data.putIntArray("pic", pixels);
                data.putInt("wth", bitmap.getWidth());
                data.putInt("hth", bitmap.getHeight());
                receiver.send(status, data);
            }
            receiver.send(7, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //ArrayList<Bitmap> ret = new ArrayList<Bitmap>();
    public Bitmap ground(String link) {
            return downloadImage(link);
    }

    public Bitmap downloadImage(String url) {
        try {
            return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());

        } catch (Exception e) {
            return null;
        }
    }
}




