package ru.ifmo.md.extratask;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by gshark on 17.03.15
 */
class WallpaperSetTask extends AsyncTask<Void, Void, Void> {
    public static final String LOG_TAG = WallpaperSetTask.class.getSimpleName();
    public static final String WALLPAPER_SET_ERROR = "Couldn't set wallpaper";
    private final Context context;
    private final Bitmap bitmap;
    private boolean success = true;

    public WallpaperSetTask(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        try {
            myWallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            Log.e(LOG_TAG, WALLPAPER_SET_ERROR);
            success = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        int res = (success ? R.string.wallpaper_success : R.string.wallpaper_fail);
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }
}
