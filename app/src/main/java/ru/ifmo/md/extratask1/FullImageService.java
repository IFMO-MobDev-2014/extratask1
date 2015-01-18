package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import ru.ifmo.md.extratask1.db.ImageContentProvider;
import ru.ifmo.md.extratask1.db.ImageDBHelper;

/**
 * Created by Mikhail on 18.01.15.
 */
public class FullImageService extends IntentService {
    public String link;
    public String myId;
    public String page;

    public FullImageService() {
        super("FullImageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        link = intent.getStringExtra("full_size_link");
        myId = intent.getStringExtra("my_id");
        page = intent.getStringExtra("page");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        receiver.send(AppResultsReceiver.STATUS_RUNNING, Bundle.EMPTY);

        try {
            Bitmap image = BitmapFactory.decodeStream(new URL(link).openConnection().getInputStream());
            saveImageToInternalStorage(image, Integer.parseInt(myId));

            ContentValues values = new ContentValues();
            values.put(ImageDBHelper.COLUMN_NAME_MY_ID, myId);
            values.put(ImageDBHelper.COLUMN_NAME_FULL_SIZE_LINK, link);
            values.put(ImageDBHelper.COLUMN_NAME_FULL_SIZE, "yes");
            getContentResolver().update(ImageContentProvider.LINK_CONTENT_URL, values,
                    ImageDBHelper.COLUMN_NAME_MY_ID + "=?", new String[] {myId});

            receiver.send(AppResultsReceiver.STATUS_FINISHED, Bundle.EMPTY);
        } catch (IOException e) {
            receiver.send(AppResultsReceiver.STATUS_INTERNET_ERROR, Bundle.EMPTY);
        }
    }

    FileOutputStream fos;
    public void saveImageToInternalStorage(Bitmap bitmap, int id) {
        try {
            fos = this.openFileOutput("f" + Integer.toString(id), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
