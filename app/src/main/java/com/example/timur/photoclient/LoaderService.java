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
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.os.Handler;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;


public class LoaderService extends IntentService {
    public static final String ID = "id";
    public static final String DATABASE_ID = "dbId";
    public static final String PAGE = "page";
    public static final String UPDATE = "update";
    public static final String WALLPAPER = "wallpaper";
    public static final String SAVE = "save";
    public static final String BROUSE = "brouse";
    public static final String TITLE = "title";
    private static Handler handler;
    public int photosPerPage = 12;
    public LoaderService() {
        super("LoaderService");
    }

    public static void setHandler(Handler handler) {
        LoaderService.handler = handler;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String id = intent.getStringExtra(ID);
            int databaseId = intent.getIntExtra(DATABASE_ID, 0);
            int page = intent.getIntExtra(PAGE, 1);
            boolean update = intent.getBooleanExtra(UPDATE, false);
            boolean wallpaper = intent.getBooleanExtra(WALLPAPER, false);
            boolean save = intent.getBooleanExtra(SAVE, false);

            if (id != null) {
                Uri uri = ContentUris.withAppendedId(DatabaseContentProvider.PHOTOS_CONTENT_URI, databaseId);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor.getCount() != 0) {
                    cursor.moveToNext();
                    ContentValues contentValues = new ContentValues();
                    String url = cursor.getString(3);
                    Bitmap bmp = fetchImage(url);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte imageInByte[] = stream.toByteArray();
                    contentValues.put(PhotoTable.AUTHOR, cursor.getString(1));
                    contentValues.put(PhotoTable.BROWSE_URL, cursor.getString(8));
                    contentValues.put(PhotoTable.PHOTOSTREAM_ID, cursor.getInt(7));
                    contentValues.put(PhotoTable.IN_FLOW_ID, cursor.getInt(6));
                    contentValues.put(PhotoTable.IMAGE_MEDIUM, cursor.getBlob(4));
                    contentValues.put(PhotoTable.ID, id);
                    contentValues.put(PhotoTable.LARGE_URL, url);
                    contentValues.put(PhotoTable.IMAGE_LARGE, imageInByte);
                    getContentResolver().update(uri, contentValues, null, null);
                }
                cursor.close();

            } else if (!wallpaper) {
                Flickr flickr = new Flickr(PhotosActivity.API_KEY, PhotosActivity.API_SECRET_KEY);
                try {
                    Cursor cursor = getContentResolver().query(DatabaseContentProvider.PHOTOS_CONTENT_URI, new String[]{PhotoTable.IN_FLOW_ID},
                            PhotoTable.PAGE + " = " + page, null, null);
                    if (cursor.getCount() < photosPerPage || update) {
                        if (update) {
                            getContentResolver().delete(DatabaseContentProvider.PHOTOS_CONTENT_URI, PhotoTable.PAGE + " = " + page, null);
                        }
                        Set<String> extras = new TreeSet<>();
                        extras.add("description");
                        extras.add("owner_name");
                        extras.add("url_l");
                        extras.add("url_c");
                        extras.add("url_q");
                        Cursor cursor1 = getContentResolver().query(DatabaseContentProvider.PHOTOS_CONTENT_URI, new String[]{PhotoTable.IN_FLOW_ID},
                                PhotoTable.PHOTOSTREAM_ID + " = " + 0, null, null);
                        int count = cursor1.getCount();
                        cursor1.close();
                        String nullString = null;
                        PhotoList photos = flickr.getInterestingnessInterface().getList(nullString, extras, photosPerPage, page);
                        ContentValues contentValues = new ContentValues();

                        for (int i = 0; i < photos.size(); ++i) {
                            Photo photo = photos.get(i);
                            Bitmap bmp = fetchImage(photo.getLargeSquareUrl());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte imageInByte[] = stream.toByteArray();
                            contentValues.put(PhotoTable.AUTHOR, photo.getOwner().getUsername());
                            contentValues.put(PhotoTable.IMAGE_MEDIUM, imageInByte);
                            contentValues.put(PhotoTable.ID, photo.getId());
                            contentValues.put(PhotoTable.LARGE_URL, photo.getLargeUrl());
                            contentValues.put(PhotoTable.BROWSE_URL, photo.getUrl());
                            contentValues.put(PhotoTable.PHOTOSTREAM_ID, 0);
                            contentValues.put(PhotoTable.IN_FLOW_ID, count + 1 + i);
                            contentValues.put(PhotoTable.PAGE, page);
                            if (handler != null) {
                                handler.obtainMessage(0).sendToTarget();
                            }
                            getContentResolver().insert(DatabaseContentProvider.PHOTOS_CONTENT_URI, contentValues).getLastPathSegment();
                        }
                        if (handler != null) {
                            handler.obtainMessage(1).sendToTarget();
                        }
                    }
                    cursor.close();
                } catch (Exception e) {
                }
            } else if (wallpaper || save) {
                Uri uri = ContentUris.withAppendedId(DatabaseContentProvider.PHOTOS_CONTENT_URI, databaseId);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor.getCount() != 0) {
                    cursor.moveToNext();
                    byte img[] = cursor.getBlob(5);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(img);
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    if (!save) {
                        try {
                            WallpaperManager.getInstance(getApplicationContext()).setBitmap(bmp);
                        } catch (IOException e) {
                        }
                    } else {
                        try {
                            String root = Environment.getExternalStorageDirectory().toString();
                            File myDir = new File(root + "/saved_images");
                            myDir.mkdirs();
                            FileOutputStream fileOutputStream;
                            File file = new File(myDir, cursor.getString(2) + ".jpg");
                            if (file.exists()) file.delete();
                            fileOutputStream = new FileOutputStream(file);
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (Exception e) {
                        }
                    }
                }
                cursor.close();

            }
        }
    }

    public static Bitmap fetchImage(String url) {
        final HttpClient client = AndroidHttpClient.newInstance("Android");
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
        } catch (IOException | IllegalStateException e) {
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }
}
