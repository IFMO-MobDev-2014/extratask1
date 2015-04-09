package ru.ifmo.ctddev.filippov.extratask1;

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
import android.util.Log;

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

/**
 * Created by Dima_2 on 01.03.2015.
 */
public class MyIntentService extends IntentService {
    private static Handler handler;
    public static final int photosOnPage = 10;

    public MyIntentService() {
        super("MyIntentService");
    }

    public static void setHandler(Handler handler) {
        MyIntentService.handler = handler;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String mId = intent.getStringExtra("id");
            int databaseId = intent.getIntExtra("databaseId", 0);
            int page = intent.getIntExtra("page", 1);
            boolean update = intent.getBooleanExtra("update", false);
            boolean wallpaper = intent.getBooleanExtra("wallpaper", false);
            boolean save = intent.getBooleanExtra("save", false);

            if (mId != null) {
                Uri uri = ContentUris.withAppendedId(Provider.PHOTOS_CONTENT_URI, databaseId);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor.getCount() != 0) {
                    cursor.moveToNext();
                    ContentValues contentValues = new ContentValues();
                    String url = cursor.getString(3);
                    Bitmap bitmap = downloadImage(url);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    contentValues.put(MyContentProvider.PHOTO_KEY_AUTHOR, cursor.getString(1));
                    contentValues.put(MyContentProvider.PHOTO_KEY_IMAGE_MEDIUM, cursor.getBlob(4));
                    contentValues.put(MyContentProvider.PHOTO_KEY_ID, mId);
                    contentValues.put(MyContentProvider.PHOTO_KEY_LARGE_URL, url);
                    contentValues.put(MyContentProvider.PHOTO_KEY_BROWSE_URL, cursor.getString(8));
                    contentValues.put(MyContentProvider.PHOTO_KEY_PHOTOSTREAM_ID, cursor.getInt(7));
                    contentValues.put(MyContentProvider.PHOTO_KEY_IN_FLOW_ID, cursor.getInt(6));
                    contentValues.put(MyContentProvider.PHOTO_KEY_IMAGE_LARGE, imageInByte);

                    getContentResolver().update(uri, contentValues, null, null);
                }
                cursor.close();
            } else if (!wallpaper) {
                Flickr flickr = new Flickr(MainActivity.API_KEY, MainActivity.API_SECRET_KEY);

                try {
                    Cursor newCursor = getContentResolver().query(Provider.PHOTOS_CONTENT_URI, new String[]{MyContentProvider.PHOTO_KEY_IN_FLOW_ID},
                            MyContentProvider.PHOTO_KEY_PAGE + " = " + page, null, null);
                    if (newCursor.getCount() < photosOnPage || update) {
                        if (update) {
                            getContentResolver().delete(Provider.PHOTOS_CONTENT_URI, MyContentProvider.PHOTO_KEY_PAGE + " = " + page, null);
                        }

                        Set<String> extras = new TreeSet<String>();
                        extras.add("description");
                        extras.add("owner_name");
                        extras.add("url_l");
                        extras.add("url_c");
                        extras.add("url_q");

                        Cursor cursor = getContentResolver().query(Provider.PHOTOS_CONTENT_URI, new String[]{MyContentProvider.PHOTO_KEY_IN_FLOW_ID},
                                MyContentProvider.PHOTO_KEY_PHOTOSTREAM_ID + " = " + 0, null, null);
                        int count = cursor.getCount();
                        cursor.close();
                        PhotoList photos = flickr.getInterestingnessInterface().getList((String) null, extras, photosOnPage, page);
                        ContentValues contentValues = new ContentValues();

                        for (int i = 0; i < photos.size(); ++i) {
                            Photo photo = photos.get(i);

                            Bitmap bitmap = downloadImage(photo.getLargeSquareUrl());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] imageInByte = stream.toByteArray();

                            contentValues.put(MyContentProvider.PHOTO_KEY_AUTHOR, photo.getOwner().getUsername());
                            contentValues.put(MyContentProvider.PHOTO_KEY_IMAGE_MEDIUM, imageInByte);
                            contentValues.put(MyContentProvider.PHOTO_KEY_ID, photo.getId());
                            contentValues.put(MyContentProvider.PHOTO_KEY_LARGE_URL, photo.getLargeUrl());
                            contentValues.put(MyContentProvider.PHOTO_KEY_BROWSE_URL, photo.getUrl());
                            contentValues.put(MyContentProvider.PHOTO_KEY_PHOTOSTREAM_ID, 0);
                            contentValues.put(MyContentProvider.PHOTO_KEY_IN_FLOW_ID, count + 1 + i);
                            contentValues.put(MyContentProvider.PHOTO_KEY_PAGE, page);

                            if (handler != null) {
                                handler.obtainMessage(0).sendToTarget();
                            }
                            getContentResolver().insert(Provider.PHOTOS_CONTENT_URI, contentValues).getLastPathSegment();
                        }
                        if (handler != null) {
                            handler.obtainMessage(1).sendToTarget();
                        }
                    }
                    newCursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (wallpaper || save) {
                Uri uri = ContentUris.withAppendedId(Provider.PHOTOS_CONTENT_URI, databaseId);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor.getCount() != 0) {
                    cursor.moveToNext();
                    byte[] image = cursor.getBlob(5);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    if (!save) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        try {
                            wallpaperManager.setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            String root = Environment.getExternalStorageDirectory().toString();
                            Log.i("root directory: ", root);
                            File directory = new File(root, "/saved_images");
                            assert directory.mkdirs();
                            File photo = new File(directory, cursor.getString(2) + ".jpg");
                            if (photo.exists()) {
                                assert photo.delete();
                            }
                            FileOutputStream photoOutputFile = new FileOutputStream(photo);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoOutputFile);
                            photoOutputFile.flush();
                            photoOutputFile.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                cursor.close();
            }
        }
    }

    public static Bitmap downloadImage(String url) {
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
        } catch (Exception e) {
            getRequest.abort();
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }
}
