package ru.ya.fotki;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.ya.fotki.activities.MainActivity;
import ru.ya.fotki.database.FotkiContentProvider;
import ru.ya.fotki.database.FotkiSQLiteHelper;

public class UpdateIntentService extends IntentService {
    public static final String ON_POST_EXECUTE = "ON_POST_EXECUTE111";
    public static final String ENTRIES = "entries";

    public UpdateIntentService() {
        super("UpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DefaultHttpClient client = new DefaultHttpClient();
        int cntPictures = intent.getIntExtra(MainActivity.COUNT_PICTURE_FOR_DOWNLOAD, -1);
        if (cntPictures == -1) throw new Error();
        HttpGet httpGet = new HttpGet("http://api-fotki.yandex.ru/api/top/?format=json;limit=100");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();

                JSONObject jsonObject = new JSONObject(result);

                JSONArray entries = jsonObject.getJSONArray(ENTRIES);
                OnePicture[] pictures = new OnePicture[entries.length()];
                for (int i = 0; i < entries.length(); i++) {
                    JSONObject entry = entries.getJSONObject(i);
                    String yandexId = entry.getString("id");
                    String httpS = entry.getJSONObject("img").getJSONObject("S").getString("href");
                    String httpXL = entry.getJSONObject("img").getJSONObject("XL").getString("href");
                    pictures[i] = new OnePicture(httpS, httpXL, yandexId);
                }
//                Log.e("start Intent Service: ", "4");
                int cnt = 0;
                for (OnePicture picture : pictures) {
                    String selection = FotkiSQLiteHelper.COLUMN_YANDEX_ID + "=?";
                    String[] selectionArgs = {picture.getYandexId()};
                    //Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null, selection, selectionArgs, null);
                    Cursor cursor = getContentResolver().query(FotkiContentProvider.FOTKI_URI, null, selection, selectionArgs, null);
                    if (cursor.getCount() == 1) {
                        picture.setAlreadyLoad(true);
                    }
                    else
                        cnt++;
                }
                ArrayList < OnePicture > secondArray = new ArrayList<>();
                for (OnePicture picture : pictures)
                    if (!picture.getAlreadyLoad() && cntPictures > 0) {
                        secondArray.add(picture);
                        cntPictures--;
                    }

                Intent resultIntent = new Intent(ON_POST_EXECUTE);
                resultIntent.putExtra(ON_POST_EXECUTE, secondArray);
                sendBroadcast(resultIntent);
                return;
            } else {
                throw new Error("failed to download file");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Log.e("something bad: ", "in intent service");
    }

}
