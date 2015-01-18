package daria.extratask;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by daria on 18.01.15.
 */
public class DownloadImagesTask extends AsyncTask<Photo, Void, Photo> {
    PhotosActivity activity;
    public static final String TAG = "500px";

    public DownloadImagesTask(PhotosActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Photo doInBackground(Photo... photos) {
        Photo photo = photos[0];
        try {
            String thumbnailURL = photo.getThumbnailURL();
            String fullURL = photo.getFullImageURL();
            InputStream isFull = (InputStream) new URL(fullURL).getContent();
            InputStream isThumbnail = (InputStream) new URL(thumbnailURL).getContent();
            photo.setThumbnail(BitmapFactory.decodeStream(isThumbnail));
            photo.setFullImage(BitmapFactory.decodeStream(isFull));
            return photo;
        } catch (Exception e) {
            Log.e(TAG, "Download Failed.", e);
            return photo;
        }
    }

    @Override
    protected void onPostExecute(Photo photo) {
        super.onPostExecute(photo);
        activity.onImageDownloaded(photo);
    }
}
