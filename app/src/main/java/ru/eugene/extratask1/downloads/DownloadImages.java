package ru.eugene.extratask1.downloads;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ru.eugene.extratask1.db.ImageDataSource;
import ru.eugene.extratask1.db.ImageItem;
import ru.eugene.extratask1.db.ImageProvider;

/**
 * Created by eugene on 1/18/15.
 */
public class DownloadImages {
    private final Context context;
    private ProgressDialog pd;
    private DownloadManager downloadManager;
    private ArrayList<ImageItem> imagesForAdapter = new ArrayList<>();
    private HashMap<Long, ImageItem> idToImageItem = new HashMap<>();
    private HashMap<Long, Boolean> used = new HashMap<>();
    public static final int CNT_IMAGES_ON_PAGE = 12;
    private ImageItem globalItem = null;
    private Boolean isRegister = false;
    private int cntNewImage;

    public void setPd(ProgressDialog pd) {
        this.pd = pd;
    }

    public DownloadImages(Context context, ProgressDialog pd) {
        this.context = context;
        this.pd = pd;
        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
    }

    BroadcastReceiver receiverDownloadService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!DownloadService.READY) {
                Log.e("LOG", "DonwloadService.READY = false");
                return;
            }
//            Log.e("LOG", "receiveDownloadService");
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) intent.getSerializableExtra(DownloadService.RESULT);
            if (images == null) return;

            cntNewImage = CNT_IMAGES_ON_PAGE;
            pd.setProgress(0);
            pd.setMax(cntNewImage);

            for (int i = 0; i < images.size() && cntNewImage > 0; i++) {
                ImageItem localItem = images.get(i);
                String url = localItem.getThumbnailUrl();
                Cursor tempCursor = context.getContentResolver().query(
                        ImageProvider.CONTENT_URI_IMAGE, null, ImageDataSource.COLUMN_IMAGE_ID + "=?",
                        new String[] {localItem.getImageId()}, null);
                if (!(tempCursor == null || tempCursor.getCount() == 0)) {
                    tempCursor.close();
                    continue;
                }
                tempCursor.close();
                DownloadManager.Request request = new DownloadManager
                        .Request(Uri.parse(url))
                        .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "imagesForAdapter");
                long id = downloadManager.enqueue(request);
                idToImageItem.put(id, localItem);
                cntNewImage--;
            }
            cntNewImage = CNT_IMAGES_ON_PAGE - cntNewImage;
            pd.setMax(cntNewImage);
            if (cntNewImage == 0) {
                pd.dismiss();
            }
        }
    };

    BroadcastReceiver receiverDownloadManager = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (idToImageItem.containsKey(reference) && !used.containsKey(reference)) {
                used.put(reference, true);
                pd.setIndeterminate(false);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(reference);
                Cursor cursor = downloadManager.query(query);

                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);

                int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                String savedFilePath = cursor.getString(fileNameIndex);

                ImageItem curItem = idToImageItem.get(reference);
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:

                        if (globalItem == null) {
                            curItem.setThumbnail(savedFilePath);
                            context.getContentResolver().insert(ImageProvider.CONTENT_URI_IMAGE,
                                    curItem.generateContentValues());
                        } else {
                            curItem.setBigImage(savedFilePath);
                            context.getContentResolver().update(ImageProvider.CONTENT_URI_IMAGE,
                                    curItem.generateContentValues(),
                                    ImageDataSource.COLUMN_IMAGE_ID + "=?", new String[]{curItem.getImageId()});
                        }
                        break;
                    case DownloadManager.STATUS_FAILED:
                        Log.e("LOG", "DownloadManager: FAILED");
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        Log.e("LOG", "DownloadManager: PAUSED");
                        break;
                    case DownloadManager.STATUS_PENDING:
                        Log.e("LOG", "DownloadManager: PENDING");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        Log.e("LOG", "DownloadManager: RUNNING");
                        break;
                }
                cursor.close();
                pd.incrementProgressBy(1);
                if (pd.getProgress() == pd.getMax()) {
                    pd.dismiss();
                }
            }
        }
    };

    public void registerMe() {
        if (isRegister) return;
        isRegister = true;
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiverDownloadManager, intentFilter);

        intentFilter = new IntentFilter(DownloadService.NOTIFICATION);
        context.registerReceiver(receiverDownloadService, intentFilter);
    }

    public void unRegisterMe() {
        if (!isRegister) return;
        isRegister = false;
        context.unregisterReceiver(receiverDownloadService);
        context.unregisterReceiver(receiverDownloadManager);
    }

    public void removeAllServices() {
        context.stopService(new Intent(context, DownloadService.class));

        int n = 0;
        for (Long id : idToImageItem.keySet())
            if (!used.containsKey(id))
                n++;

        if (n > 0) {
            long ids[] = new long[n];
            for (Long id : idToImageItem.keySet())
                if (!used.containsKey(id))
                    ids[--n] = id;
            downloadManager.remove(ids);
        }
        pd.dismiss();
    }

    public void updateImages(Cursor data) {
        imagesForAdapter.clear();
        if (data != null && data.moveToFirst()) {
            do {
                imagesForAdapter.add(ImageDataSource.generateImageItem(data));
            } while (data.moveToNext());
        }
    }

    public ArrayList<ImageItem> getImages() {
        return imagesForAdapter;
    }

    public void startDownload(ImageItem curItem) {
        Log.e("LOG", "DownloadImages.globalItem.url:" + curItem.getBigImageUrl());
        this.globalItem = curItem;
        DownloadManager.Request request = new DownloadManager
                .Request(Uri.parse(curItem.getBigImageUrl()))
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "images");
        long id = downloadManager.enqueue(request);
        idToImageItem.put(id, this.globalItem);
    }

    public void startDownload() {
        context.startService(new Intent(context, DownloadService.class));
    }
}
