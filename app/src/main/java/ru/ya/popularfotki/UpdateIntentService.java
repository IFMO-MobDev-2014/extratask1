package ru.ya.popularfotki;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Picture;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.ya.popularfotki.database.FotkiContentProvider;
import ru.ya.popularfotki.database.FotkiSQLiteHelper;

public class UpdateIntentService extends IntentService {
    public static final String ON_POST_EXECUTE = "ON_POST_EXECUTE";
    public static final String ENTRIES = "entries";
    public UpdateIntentService() {
        super("UpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("start Intent Service: ", "1");
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httpPost = new HttpPost("http://api-fotki.yandex.ru/api/top/?format=json;limit=2");
        httpPost.setHeader("Content-type", "application/json");

        InputStream inputStream;
        String result = null;
        HttpResponse response;



        Log.e("start Intent Service: ", "2");



        try {
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                Log.e("next line: ", line);
                sb.append(line).append("\n");
            }
            result = sb.toString();
            Log.e("start Intent Service: ", "3");
            Log.e("json:", ":" + result + ":\n");
            JSONObject jsonObject = new JSONObject(result);

            JSONArray entries = jsonObject.getJSONArray(ENTRIES);
            OnePicture [] pictures = new OnePicture[entries.length()];
            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                String yandexId = entry.getString("id");
                String httpS = entry.getJSONObject("img").getJSONObject("S").getString("href");
                String httpXL = entry.getJSONObject("img").getJSONObject("XL").getString("href");
                pictures[i] = new OnePicture(httpS, httpXL, yandexId);
            }
            Log.e("start Intent Service: ", "4");
            int cnt = 0;
            for (OnePicture picture: pictures) {
                String selection = FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?";
                String [] selectionArgs = {picture.getYandexId()};
                Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null, selection, selectionArgs, null);
                if (cursor.getCount() == 1) {
                    picture.setAlreadyLoad(true);
                    cnt++;
                }
            }
            OnePicture [] secondArray = new OnePicture[cnt];
            int cur = 0;
            for (OnePicture picture: pictures)
                if (picture.getAlreadyLoad() == false)
                    secondArray[cur++] = picture;

            Intent resultIntent = new Intent(ON_POST_EXECUTE);
            resultIntent.putExtra(ON_POST_EXECUTE, secondArray);
            return;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Log.e("something bad: ", "in intent service");
    }

}
