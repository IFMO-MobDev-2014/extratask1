package year2013.ifmo.photogallery;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class ImageIntentService extends IntentService {
    public static final String EXTRA_PROGRESS = "extra_percents";
    public static final String EXTRA_LARGE = "extra_large";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_ORIG = "extra_orig";
    public static final String EXTRA_TITLE = "extra_title";

    private static final String YANDEX_API =
            "http://api-fotki.yandex.ru/api/podhistory/?format=json";

    public static final String BROADCAST_ACTION =
            "year2013.ifmo.photogallery.BROADCAST";
    public static final String BROADCAST_LARGE =
            "year2013.ifmo.photogallery.BROADCAST_LARGE";

    public static final String ACTION_ALL = "year2013.ifmo.photogallery.extratask1.ACTION_ALL";
    public static final String ACTION_LARGE = "year2013.ifmo.photogallery.extratask1.ACTION_LARGE";
    public static final String ACTION_ORIG = "year2013.ifmo.photogallery.extratask1.ACTION_ORIG";
    public static final String ACTION_SET_WALLPAPER = "year2013.ifmo.photogallery.extratask1.ACTION_SET_WALLPAPER";


    private Handler handler = new Handler();

    public ImageIntentService() {
        super("ImageIntentService");
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String title){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, title + ".jpg");
        FileOutputStream out;
        try {
            out = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private void saveToExternalStorage(Bitmap bitmapImage, String title, String action) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File directory = new File(root + "/PhotoGallery");
        //directory.mkdirs();
        File file = new File (directory, title + ".jpg");
        //if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            showToast(getString(R.string.error_message));
            e.printStackTrace();
        }
        if (ACTION_SET_WALLPAPER.equals(action)) {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            try {
                manager.setBitmap(bitmapImage);
                showToast(getString(R.string.done));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else showToast(getString(R.string.save));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                String action = intent.getAction();
                byte[] bytes;
                String title;
                ByteArrayInputStream imageStream;
                Bitmap theImage;
                switch(action) {
                    case ACTION_ALL: {
                        downloadAll();
                        break;
                    }
                    case ACTION_LARGE: {
                        bytes = getImage(intent.getStringExtra(EXTRA_LARGE));
                        imageStream = new ByteArrayInputStream(bytes);
                        theImage = BitmapFactory.decodeStream(imageStream);
                        long id = intent.getLongExtra(EXTRA_ID, 1);
                        title = intent.getStringExtra(EXTRA_TITLE);
                        String path = saveToInternalStorage(theImage, title);
                        Uri uri = ContentUris.withAppendedId(Gallery.Images.CONTENT_URI, id);
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        ContentValues cv = new ContentValues();
                        cv.put(Gallery.Images.LARGE_PATH_NAME, path);
                        getContentResolver().update(uri, cv, null, null);
                        Intent localIntent =
                                new Intent(BROADCAST_LARGE);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                        break;
                    }
                    case ACTION_ORIG:
                    case ACTION_SET_WALLPAPER: {
                        title = intent.getStringExtra(EXTRA_TITLE);
                        //long id = intent.getLongExtra(EXTRA_ID, 1);
                        String root = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).toString();
                        File directory = new File(root + "/PhotoGallery");
                        //directory.mkdirs();
                        File file = new File(directory, title + ".jpg");

                        if (file.exists()) {
                            try {
                                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file));
                                if (ACTION_ORIG.equals(action)) {
                                    showToast(getString(R.string.exists));
                                }

                                if (ACTION_SET_WALLPAPER.equals(action)) {
                                    WallpaperManager manager = WallpaperManager.getInstance(this);
                                    manager.setBitmap(image);
                                    showToast(getString(R.string.done));
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast(getString(R.string.wait));
                            bytes = getImage(intent.getStringExtra(EXTRA_ORIG));
                            imageStream = new ByteArrayInputStream(bytes);
                            try {
                                theImage = BitmapFactory.decodeStream(imageStream);
                                saveToExternalStorage(theImage, title, action);
                            } catch (OutOfMemoryError e) {
                                showToast(getString(R.string.error_message));
                            }
                            //long id = intent.getLongExtra(EXTRA_ID, 1);
                            sendBroadcast(new Intent(
                                    Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            showToast(getString(R.string.error_message));
            e.printStackTrace();
        }
    }

    private void downloadAll() throws IOException, JSONException{
        JSONArray images = load(YANDEX_API).getJSONArray("entries");

        Cursor cursor = getContentResolver()
                .query(Gallery.Images.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        String prev_update = "";
        if (!cursor.isAfterLast()) {
            prev_update = cursor.getString(cursor.getColumnIndex(Gallery.Images.LAST_UPDATE));
            cursor.close();
        }

        int l = images.length();
        String last_update = new Date().getTime() + "t";
        for (int i = 0; i < l; i++) {

            JSONObject image = images.getJSONObject(i);

            String title = image.getString("title");

            image = image.getJSONObject("img");

            String smallURL = image.getJSONObject("M").getString("href");
            String largeURL = image.getJSONObject("L").getString("href");
            String origURL;
            if (!image.isNull("orig")) {
                origURL = image.getJSONObject("orig").getString("href");
            } else {
                origURL = image.getJSONObject("XXXL").getString("href");
            }


            ContentValues cv = new ContentValues();
            cv.put(Gallery.Images.SMALL_IMAGE, getImage(smallURL));
            cv.put(Gallery.Images.LARGE_IMAGE_URL, largeURL);
            cv.put(Gallery.Images.ORIG_IMAGE_URL, origURL);
            cv.put(Gallery.Images.TITLE, title);
            cv.put(Gallery.Images.LAST_UPDATE, last_update);
            getContentResolver().insert(Gallery.Images.CONTENT_URI, cv);

            Intent localIntent =
                    new Intent(BROADCAST_ACTION)
                            .putExtra(EXTRA_PROGRESS, 100 * (i + 1) / l);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

        getContentResolver().delete(Gallery.Images.CONTENT_URI,
                Gallery.Images.LAST_UPDATE + "=\"" + prev_update + "\"", null);
    }

    private byte[] getImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        HttpURLConnection ucon =
                (HttpURLConnection) imageUrl.openConnection();

        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);

        ByteArrayBuffer baf = new ByteArrayBuffer(1000);
        int current;
        while ((current = bis.read()) != -1) {
            baf.append((byte) current);
        }

        return baf.toByteArray();
    }

    private JSONObject load(String from) throws IOException, JSONException {
        URL url = new URL(from);
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        StringBuilder json = new StringBuilder(1024);
        String tmp;
        while ((tmp = reader.readLine()) != null)
            json.append(tmp).append("\n");
        reader.close();

        return new JSONObject(json.toString());
    }

    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ImageIntentService.this,
                        text,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}