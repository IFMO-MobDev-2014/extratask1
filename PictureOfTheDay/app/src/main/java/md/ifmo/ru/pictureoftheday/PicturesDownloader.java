package md.ifmo.ru.pictureoftheday;

/**
 * Created by Илья on 17.01.2015.
 */

import android.app.IntentService;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PicturesDownloader extends IntentService {
    public static String serviceName = "picturesDownloader";
    public static final String ACTION_RESPONSE = "md.ifmo.ru.pictureoftheday.picturesDownloader.RESPONSE";
    public static final int RESULT_ERROR = -1;
    public static final String TAG_PERCENT = "percent";
    public static final String requestUrl = "http://api-fotki.yandex.ru/api/podhistory/?format=json";
    public static final String reducedSize = "M";
    public static final String fullSize = "XXL";

    public PicturesDownloader() {
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
                    PicturesContentProvider.PICTURES_URI,
                    null,
                    null
            );

            StringBuilder builder = new StringBuilder();
            URLConnection connection = new URL(requestUrl).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();

            String resultJson = builder.toString();
            JSONObject jsonObject = new JSONObject(resultJson);
            JSONArray entries = jsonObject.getJSONArray("entries");

            String downloadLink;
            String hrLink;
            String pageLink;
            String title;
            JSONObject picture;
            Bitmap bitmap;
            ByteArrayOutputStream bos;

            for (int i = 0; i < entries.length(); i++) {
                picture = entries.getJSONObject(i);

                downloadLink = picture.getJSONObject("img").getJSONObject(reducedSize).getString("href");
                hrLink = picture.getJSONObject("img").getJSONObject(fullSize).getString("href");
                pageLink = picture.getJSONObject("links").getString("alternate");
                title = picture.getString("title");

                URL picUrl = new URL(downloadLink);
                bitmap = BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
                ContentValues cv = new ContentValues();

                bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth() ,bitmap.getWidth() , ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bArray = bos.toByteArray();

                cv.put(DBPictures.COLUMN_PICTURE, bArray);
                cv.put(DBPictures.COLUMN_PICTURE_HR, hrLink);
                cv.put(DBPictures.COLUMN_PICTURE_LINK, pageLink);
                cv.put(DBPictures.COLUMN_PICTURE_TITLE, title);

                getContentResolver().insert(PicturesContentProvider.PICTURES_URI, cv);

                Intent response2 = new Intent();
                response2.setAction(ACTION_RESPONSE);
                response2.addCategory(Intent.CATEGORY_DEFAULT);
                response2.putExtra(TAG_PERCENT, i);
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
