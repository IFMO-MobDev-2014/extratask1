package com.example.timur.photoclient;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


public class LoaderService extends IntentService {
    public static final String ID = "id";
    public static final String DATABASE_ID = "dbId";
    public static final String PHOTO_PER_PAGE = "photo_per_page";
    public static final String PAGE = "page";
    public static final String UPDATE = "update";
    public static final String WALLPAPER = "wallpaper";
    public static final String SAVE = "save";
    public static final String BROUSE = "brouse";
    public static final String TITLE = "title";
    public static final String API_KEY = "2c0b4b4e1a4d7501b585dd765bd0857f";
    public static final String API_SECRET_KEY = "3cdb99ca3567fb1f";

    private static Handler handler;
    public static final int MESSAGE_PROGRESS = 0;
    public static final int MESSAGE_FINISHED = 1;

    public static final String ACTION_SET_WALLPAPER = "SET_WALLPAPER";
    public static final String ACTION_SAVE = "SAVE";
    public static final String ACTION_DOWNLOAD_PAGE = "DOWNLOAD_PAGE";
    public static final String ACTION_DOWNLOAD_PHOTO = "DOWNLOAD_PHOTO";


    public LoaderService() {
        super("LoaderService");
    }

    public static void setHandler(Handler handler) {
        LoaderService.handler = handler;
    }

    private void downloadPhoto(Intent intent) {
        try {
            Uri uri = ContentUris.withAppendedId(DatabaseContentProvider.PHOTOS_CONTENT_URI,
                    intent.getIntExtra(DATABASE_ID, 0));
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                ContentValues contentValues = new ContentValues();
                String url = cursor.getString(4);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                downloadImage(url).compress(Bitmap.CompressFormat.PNG, 100, stream);
                contentValues.put(PhotoTable.AUTHOR, cursor.getString(3));
                contentValues.put(PhotoTable.IMAGE_MEDIUM, cursor.getBlob(2));
                contentValues.put(PhotoTable.BROWSE_URL, cursor.getString(8));
                contentValues.put(PhotoTable.PHOTO_STREAM_ID, cursor.getInt(7));
                contentValues.put(PhotoTable.IN_FLOW_ID, cursor.getInt(1));
                contentValues.put(PhotoTable.ID, intent.getStringExtra(ID));
                contentValues.put(PhotoTable.URL, url);
                contentValues.put(PhotoTable.IMAGE_LARGE, stream.toByteArray());
                getContentResolver().update(uri, contentValues, null, null);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWallpaper(Intent intent) {
        Uri uri = ContentUris.withAppendedId(DatabaseContentProvider.PHOTOS_CONTENT_URI,
                intent.getIntExtra(DATABASE_ID, 0));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            ByteArrayInputStream imageStream = new ByteArrayInputStream(cursor.getBlob(5));
            try {
                WallpaperManager.getInstance(getApplicationContext()).setBitmap(BitmapFactory.decodeStream(imageStream));
            } catch (IOException e) {
            }
        }
        cursor.close();
    }

    private void savePhoto(Intent intent) {
        int databaseId = intent.getIntExtra(DATABASE_ID, 0);
        Uri uri = ContentUris.withAppendedId(DatabaseContentProvider.PHOTOS_CONTENT_URI, databaseId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            ByteArrayInputStream imageStream = new ByteArrayInputStream(cursor.getBlob(5));
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/saved_images");
                myDir.mkdirs();
                File file = new File(myDir, cursor.getString(2) + ".jpg");
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BitmapFactory.decodeStream(imageStream).compress(Bitmap.CompressFormat.JPEG,
                        100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
            }
        }
        cursor.close();
    }

    private void downloadPage(Intent intent) {
        int perPage = intent.getIntExtra(PHOTO_PER_PAGE, 24);
        int page = intent.getIntExtra(PAGE, 1);
        boolean update = intent.getBooleanExtra(UPDATE, false);
        Flickr flickr = new Flickr(API_KEY, API_SECRET_KEY);
        try {
            Cursor cursor = getContentResolver().query(DatabaseContentProvider.PHOTOS_CONTENT_URI, new String[]{PhotoTable.IN_FLOW_ID},
                    PhotoTable.PAGE + " = " + page, null, null);
            if (cursor.getCount() < perPage || update) {
                if (update) {
                    getContentResolver().delete(DatabaseContentProvider.PHOTOS_CONTENT_URI, PhotoTable.PAGE + " = " + page, null);
                }
                TreeSet<String> extras = new TreeSet<>();
                Collections.addAll(extras, "description", "owner_name", "url_l", "url_c", "url_q");
                int count = getContentResolver().query(DatabaseContentProvider.PHOTOS_CONTENT_URI, new String[]{PhotoTable.IN_FLOW_ID},
                        PhotoTable.PHOTO_STREAM_ID + " = " + 0, null, null).getCount();
                Date nullDate = null;
                PhotoList photos = flickr.getInterestingnessInterface().getList(nullDate, extras, perPage, page);
                ContentValues contentValues = new ContentValues();

                for (int i = 0; i < photos.size(); ++i) {
                    Photo photo = photos.get(i);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    downloadImage(photo.getLargeSquareUrl()).compress(Bitmap.CompressFormat.PNG, 100, stream);
                    contentValues.put(PhotoTable.AUTHOR, photo.getOwner().getUsername());
                    contentValues.put(PhotoTable.IMAGE_MEDIUM, stream.toByteArray());
                    contentValues.put(PhotoTable.ID, photo.getId());
                    contentValues.put(PhotoTable.URL, photo.getLargeUrl());
                    contentValues.put(PhotoTable.BROWSE_URL, photo.getUrl());
                    contentValues.put(PhotoTable.PHOTO_STREAM_ID, 0);
                    contentValues.put(PhotoTable.IN_FLOW_ID, count + 1 + i);
                    contentValues.put(PhotoTable.PAGE, page);
                    if (handler != null) {
                        handler.obtainMessage(MESSAGE_PROGRESS).sendToTarget();
                    }
                    getContentResolver().insert(DatabaseContentProvider.PHOTOS_CONTENT_URI, contentValues).getLastPathSegment();
                }
                if (handler != null) {
                    handler.obtainMessage(MESSAGE_FINISHED).sendToTarget();
                }
            }
            cursor.close();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            switch (intent.getAction()) {
                case ACTION_DOWNLOAD_PAGE:
                    downloadPage(intent);
                    break;
                case ACTION_DOWNLOAD_PHOTO:
                    downloadPhoto(intent);
                    break;
                case ACTION_SAVE:
                    savePhoto(intent);
                    break;
                case ACTION_SET_WALLPAPER:
                    setWallpaper(intent);
                    break;
            }
        } catch (NullPointerException e) {
        }
    }

    public static Bitmap downloadImage(String url) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    return BitmapFactory.decodeStream(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
        }
        return null;
    }
}
