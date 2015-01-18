package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImagesService extends IntentService {
    public static String serviceName = "downloadImagesService";
    public static final String ACTION_RESPONSE = "ru.ifmo.md.extratask1.RESPONSE";
    public static final int RESULT_ERROR = -1;
    public static final String TAG_PERCENT = "percent";
    public static final int IMAGE_NUMBER = 20;
    public static final String yandexUrl = "http://api-fotki.yandex.ru/api/podhistory/?limit=" + IMAGE_NUMBER;

    public DownloadImagesService() {
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
            getContentResolver().delete(ImagesContentProvider.IMAGES_URI, null, null);

            URL url = new URL(yandexUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = bufreader.readLine()) != null) {
                total.append(line);
            }
            xpp.setInput(new StringReader(total.toString()));
            String author;
            String title;
            URL imageUrl;
            int count = 0;
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("entry")) {
                    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xpp.getEventType() == XmlPullParser.START_TAG &&  xpp.getName().equals("name")) {
                            break;
                        }
                        xpp.next();
                    }
                    xpp.next();
                    author = xpp.getText();

                    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xpp.getEventType() == XmlPullParser.START_TAG &&  xpp.getName().equals("title")) {
                            break;
                        }
                        xpp.next();
                    }
                    xpp.next();
                    title = xpp.getText();

                    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xpp.getEventType() == XmlPullParser.START_TAG &&  xpp.getName().equals("img") && xpp.getAttributeCount() > 3 && xpp.getAttributeValue(2).equals("L")) {
                            break;
                        }
                        xpp.next();
                    }
                    imageUrl = new URL(xpp.getAttributeValue(1));
                    Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                    ContentValues contentValues = new ContentValues();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] byteArray = bos.toByteArray();
                    contentValues.put(ImagesSQLite.COLUMN_PICTURE, byteArray);
                    contentValues.put(ImagesSQLite.COLUMN_TITLE, title);
                    contentValues.put(ImagesSQLite.COLUMN_AUTHOR, author);
                    getContentResolver().insert(ImagesContentProvider.IMAGES_URI, contentValues);
                    count++;
                    Intent response2 = new Intent();
                    response2.setAction(ACTION_RESPONSE);
                    response2.addCategory(Intent.CATEGORY_DEFAULT);
                    response2.putExtra(TAG_PERCENT, Math.round((float)count / (float)IMAGE_NUMBER * (float)100));
                    sendBroadcast(response2);
                }
                xpp.next();
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

