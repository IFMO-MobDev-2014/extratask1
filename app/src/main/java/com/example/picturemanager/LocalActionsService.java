package com.example.picturemanager;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Амир on 21.01.2015.
 */
public class LocalActionsService extends IntentService {

    public LocalActionsService() {
        super("LocalActionsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int idInDB = intent.getIntExtra("id", 0);
        Cursor cursor = getContentResolver().query(DBContentProvider.PICTURES,
                new String[]{DBHelper.PICTURES_BIG_PICTURE, DBHelper.PICTURES_NAME}, DBHelper.PICTURES_COLUMN_ID  + " = " + idInDB, null, null);
        cursor.moveToFirst();
        byte[] bArray = cursor.getBlob(cursor.getColumnIndex(DBHelper.PICTURES_BIG_PICTURE));
        String name = cursor.getString(cursor.getColumnIndex(DBHelper.PICTURES_NAME));
        Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
        if (intent.getAction().equals("save")) {
            String root = Environment.getExternalStorageDirectory().toString();
            File image = new File(root + "/saved_images");
            image.mkdirs();
            File file = new File(image, name + ".jpg");
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (intent.getAction().equals("set as wallpaper")) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
