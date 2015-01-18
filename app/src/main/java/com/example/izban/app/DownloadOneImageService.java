package com.example.izban.app;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.net.URL;

/**
 * Created by izban on 18.01.15.
 */
public class DownloadOneImageService extends IntentService {
    public DownloadOneImageService() {
        super(DownloadOneImageService.class.getSimpleName());
    }

    public DownloadOneImageService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(Constants.RECEIVER);
        try {
            Log.i("", "start DownloadOneImageService");
            receiver.send(Constants.RECEIVER_STARTED, Bundle.EMPTY);
            URL url = new URL(intent.getStringExtra("link"));
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap", bitmap);
            receiver.send(Constants.RECEIVER_FINISHED, bundle);
            Log.i("", "DownloadOneImageService ok");
        } catch (java.io.IOException e) {
            receiver.send(Constants.RECEIVER_FAILED, Bundle.EMPTY);
            Log.i("", "DownloadOneImageService failed");
        }
    }
}
