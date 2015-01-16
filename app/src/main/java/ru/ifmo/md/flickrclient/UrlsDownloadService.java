package ru.ifmo.md.flickrclient;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by sultan on 15.01.15.
 */
public class UrlsDownloadService extends IntentService {
    private static final int COUNT_IMAGES = 20;
    public static final String ACTION_RESPONSE = "ru.ifmo.md.flickrclient.urlsDownloadService.RESPONSE";
    public static final String DOWNLOAD_ID = "PHOTO_ID";
    private PhotoList photoList = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public UrlsDownloadService() {
        super("urlsDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            photoList = f.getPhotosInterface().getRecent(new HashSet<String>(), COUNT_IMAGES, 1);
            Log.d("Service", "urls");

            getContentResolver().delete(FlickrContentProvider.PHOTO_URI, null, null);
            for (Photo photo : photoList)
            {
                String strUrl = photo.getLargeSquareUrl();
                URL url = new URL(strUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream is = httpURLConnection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();

                ContentValues cv = new ContentValues();
                cv.put(DBFlickr.PHOTO_ID, photo.getId());
                cv.put(DBFlickr.PHOTO_FARM, photo.getFarm());
                cv.put(DBFlickr.PHOTO_SERVER, photo.getServer());
                cv.put(DBFlickr.PHOTO_SECRET, photo.getSecret());
                cv.put(DBFlickr.PHOTO, bytes);

                getContentResolver().insert(FlickrContentProvider.PHOTO_URI, cv);
            }
//            Intent i = new Intent(this, ImageDownloadService.class);
//            i.putExtra("photoList", photoList);
//            startService(i);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Service", "broadcast");
        Intent response = new Intent();
        response.setAction(ACTION_RESPONSE);
        response.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(response);
    }
}
