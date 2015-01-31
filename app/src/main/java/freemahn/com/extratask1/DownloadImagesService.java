package freemahn.com.extratask1;

/**
 * Created by Freemahn on 17.01.2015.
 */

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DownloadImagesService extends IntentService {
    public static String serviceName = "downloadImagesService";
    public static final String ACTION_RESPONSE = "freemahn.com.extratask1.downloadImagesService.RESPONSE";
    public static final int RESULT_ERROR = -100;
    public static final String TAG_PERCENT = "percent";
    ArrayList<Entry> entries;
    public static final String ACTION_UPDATE = "freemahn.com.extratask1.downloadImagesService.UPDATE";

    public DownloadImagesService() {
        super(serviceName);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isImageBig = intent.getBooleanExtra("imageSize", false);
        String url = "http://api-fotki.yandex.ru/api/top/";
        String resp = "";
        DefaultHttpClient hc = new DefaultHttpClient();
        ResponseHandler<String> res = new BasicResponseHandler();
        HttpGet http = new HttpGet(url);
        try {
            resp = hc.execute(http, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream stream = new ByteArrayInputStream(resp.getBytes(StandardCharsets.UTF_8));
        entries = MySAXParser.parse(stream);
        //Log.d("SERVICE", entries.size() + "");
        getContentResolver().delete(
                ImagesContentProvider.IMAGES_URI,
                null,
                null
        );
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            URL imgUrl = null;
            Bitmap bmp = null;
            try {

                imgUrl = new URL(isImageBig ? e.linkBig : e.linkSmall);
                bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
            } catch (IOException e1) {
                Intent intentError = new Intent();
                intentError.setAction(ACTION_RESPONSE);
                intentError.addCategory(Intent.CATEGORY_DEFAULT);
                intentError.putExtra(TAG_PERCENT, RESULT_ERROR);
                sendBroadcast(intentError);
                e1.printStackTrace();
            }
            ContentValues cv = new ContentValues();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            cv.put(DatabaseHelper.IMAGE_COLUMN, bos.toByteArray());
            cv.put(DatabaseHelper.TITLE_COLUMN, e.title);
            cv.put(DatabaseHelper.LINK_SMALL_COLUMN, e.linkSmall);
            cv.put(DatabaseHelper.LINK_BIG_COLUMN, e.linkBig);
            getContentResolver().insert(ImagesContentProvider.IMAGES_URI, cv);
            Intent intentUpdate = new Intent();
            intentUpdate.setAction(ACTION_UPDATE);
            intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
            intentUpdate.putExtra(TAG_PERCENT, Math.round((float) (i + 1) / (float) entries.size() * (float) 100));
            sendBroadcast(intentUpdate);
        }


        Intent response = new Intent();
        response.setAction(ACTION_RESPONSE);
        response.addCategory(Intent.CATEGORY_DEFAULT);
        response.putExtra(TAG_PERCENT, 0);
        sendBroadcast(response);
    }
}