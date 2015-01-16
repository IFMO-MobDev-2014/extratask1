package ru.ifmo.md.flickrclient;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sultan on 15.01.15.
 */
public class ImageDownloadService extends IntentService {

    private boolean original;
    private PhotoList photoList;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ImageDownloadService() {
        super("imageDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.photoList = (PhotoList) intent.getSerializableExtra("photoList");
        this.original = photoList.getTotal() == 1;
        if (original) {
        } else {
            try {
                getContentResolver().delete(FlickrContentProvider.PHOTO_URI, null, null);
                for (Photo photo : photoList)
                {
                    String strUrl = photo.getSmallSquareUrl();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
