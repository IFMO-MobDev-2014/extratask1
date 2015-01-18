package com.example.kirill.topyandexphoto;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.example.kirill.topyandexphoto.db.PhotoContentProvider;
import com.example.kirill.topyandexphoto.db.model.ImageData;
import com.example.kirill.topyandexphoto.db.model.ImageDataTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Kirill on 11.01.2015.
 */
public class LoadDataService extends IntentService {

    public static final String RESULT_TYPE = "resultType";

    public static final int FINISHED_RESULT = 1;
    public static final int ERROR_RESULT = 11;

    public static final String PREVIEW_URLS = "previewUrls";
    public static final String LOADED_COUNT = "loadedCount";
    public static final String ERROR_MSG = "errorMessage";
    public static final String NOTIFICATION = "com.example.kirill.topyandexphoto";

    public static final int LOAD_IMAGES_LIMIT = 50;

    public static final String previewSize = "M";
    public static final String fullScreenSize = "XXL";

    private String topPhotosUrl = "http://api-fotki.yandex.ru/api/top/published/?limit=" + LOAD_IMAGES_LIMIT;

    public LoadDataService() {
        super("LoadDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ArrayList<ImageData> data = loadText();
            saveToDB(data);
            ArrayList<String> loadedPreviewUrls = new ArrayList<>();
            for (ImageData i : data) {
                loadedPreviewUrls.add(i.getPreviewUrl());
            }
            publishResult(FINISHED_RESULT, loadedPreviewUrls);
        } catch (Exception e) {
            e.printStackTrace();
            publishError(ERROR_RESULT, "Unknown error");
            return;
        }
        return;
    }

    private void saveToDB(ArrayList<ImageData> data) {
        int deleted = getContentResolver()
                .delete(PhotoContentProvider.CONTENT_URI_IMAGES, ImageDataTable._ID + " != ?", new String[]{"-1"});

        for (ImageData i : data) {
            ContentValues values = new ContentValues();
            values.put(ImageDataTable.ENTRY_ID_COLUMN, i.getEntryId());
            values.put(ImageDataTable.ENTRY_URL_COLUMN, i.getEntryUrl());
            values.put(ImageDataTable.PUBLISHED_COLUMN, i.getPublished());
            values.put(ImageDataTable.TITLE_COLUMN, i.getTitle());
            values.put(ImageDataTable.AUTHOR_NAME_COLUMN, i.getAuthorName());
            values.put(ImageDataTable.PREVIEW_URL_COLUMN, i.getPreviewUrl());
            values.put(ImageDataTable.BIG_URL_COLUMN, i.getBigUrl());
            getContentResolver().insert(PhotoContentProvider.CONTENT_URI_IMAGES, values);
        }
    }

    private ArrayList<ImageData> loadText() {
        ArrayList<ImageData> rv = null;
        if (TextDownloader.isOnline(this)) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("Accept", "application/json");
            String data = TextDownloader.load(topPhotosUrl, properties);
            rv = parse(data);
        } else {
            publishError(ERROR_RESULT, "Internet connection error");
            return null;
        }
        return rv;
    }

    private ArrayList<ImageData> parse(String data) {
        int parsingErrorsCount = 0;
        ArrayList<ImageData> rv = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(data);
            JSONArray entries = root.getJSONArray("entries");
            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = null;
                try {
                    entry = entries.getJSONObject(i);
                    rv.add(parseEntry(entry));
                } catch (Exception e) {
                    parsingErrorsCount++;
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            publishError(ERROR_RESULT, "JSON parsing error");
            return null;
        }
        return rv;
    }

    private ImageData parseEntry(JSONObject entry) throws JSONException {
        ImageData rv = new ImageData();
        rv.setEntryId(entry.getString("id"));
        rv.setTitle(entry.getString("title"));
        JSONObject author = entry.getJSONArray("authors").getJSONObject(0);
        rv.setAuthorName(author.getString("name"));
        JSONObject links = entry.getJSONObject("links");
        String linkAlternative = links.getString("alternate");
        rv.setEntryUrl(linkAlternative);
        try {
            rv.setPublished(parseRFC3339Date(entry.getString("published")).getTime() / 1000L);
        } catch (ParseException e) {
            e.printStackTrace();
            rv.setPublished(0L);
        }
        JSONObject img = entry.getJSONObject("img");
        rv.setPreviewUrl(img.getJSONObject(previewSize).getString("href"));
        rv.setBigUrl(img.getJSONObject(fullScreenSize).getString("href"));
        return rv;
    }

    private void publishResult(int resultType, ArrayList<String> urls) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT_TYPE, resultType);
        intent.putStringArrayListExtra(PREVIEW_URLS, urls);
        int loadedCount = urls == null ? 0 : urls.size();
        intent.putExtra(LOADED_COUNT, loadedCount);
        sendBroadcast(intent);
    }

    private void publishError(int resultType, String msg) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT_TYPE, resultType);
        intent.putExtra(ERROR_MSG, msg);
        sendBroadcast(intent);
    }

    private static Date parseRFC3339Date(String dateString) throws ParseException, IndexOutOfBoundsException {
        Date d = new Date();
        if (dateString.endsWith("Z")) {
            try {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                d = s.parse(dateString);
            } catch (java.text.ParseException pe) {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                s.setLenient(true);
                d = s.parse(dateString);
            }
            return d;
        }
        String firstPart = dateString.substring(0, dateString.lastIndexOf('-'));
        String secondPart = dateString.substring(dateString.lastIndexOf('-'));
        secondPart = secondPart.substring(0, secondPart.indexOf(':')) + secondPart.substring(secondPart.indexOf(':') + 1);
        dateString = firstPart + secondPart;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            d = s.parse(dateString);
        } catch (java.text.ParseException pe) {
            s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
            s.setLenient(true);
            d = s.parse(dateString);
        }
        return d;
    }

}
