package ru.ifmo.md.photooftheday;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import ru.ifmo.md.photooftheday.memoryutils.FilesUtils;
import ru.ifmo.md.photooftheday.memoryutils.LoadBitmapTask;
import ru.ifmo.md.photooftheday.memoryutils.SaveBitmapTask;
import ru.ifmo.md.photooftheday.photodownloader.BitmapDownloadTask;

/**
 * Created by vadim on 22/01/15.
 */
public class Photo implements Parcelable {
    public static final String TAG = Photo.class.getSimpleName();

    public final String name;
    public final String id;
    public final URL thumbnailUrl;
    public final URL fullUrl;

    /* package-private */ static final String THUMBNAIL_SUFFIX = "-thumbnail";

    public Photo(String name, String id, URL thumbnailUrl, URL fullUrl) {
        this.name = name;
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.fullUrl = fullUrl;
    }

    public File getPathToFullBitmap() {
        return new File(FilesUtils.getApplicationStorageDir(), id);
    }

    public Bitmap getThumbnailBitmap() {
        return getBitmap(id + THUMBNAIL_SUFFIX, thumbnailUrl);
    }

    public Bitmap getFullBitmap() {
        return getBitmap(id, fullUrl);
    }

    private Bitmap getBitmap(final String fileName, final URL url) {
        Bitmap result = null;
        if (FilesUtils.fileExists(FilesUtils.getApplicationStorageDir(), fileName)) {
            try {
                result = new LoadBitmapTask(fileName).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            try {
                result = new BitmapDownloadTask(){
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        new SaveBitmapTask(fileName).execute(bitmap);
                    }
                }.execute(url).get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return result;
    }

    public static final Parcelable.Creator<Photo> CREATOR
            = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            String[] args = new String[4];
            in.readStringArray(args);
            String name = args[0];
            String id = args[1];
            URL thumbnailUrl = null;
            URL fullUrl = null;
            try {
                thumbnailUrl = new URL(args[2]);
                fullUrl = new URL(args[3]);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return new Photo(name, id, thumbnailUrl, fullUrl);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // @fiXedd said that I can ignore this method, I believe sincerely in his words
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{name, id, thumbnailUrl.toString(), fullUrl.toString()});
    }
}
