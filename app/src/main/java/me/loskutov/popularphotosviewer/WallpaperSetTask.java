package me.loskutov.popularphotosviewer;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by ignat on 17.01.15.
 */
class WallpaperSetTask extends AsyncTask<Void, Void, Void> {
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
            e.printStackTrace();
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
