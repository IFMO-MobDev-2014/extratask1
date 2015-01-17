package ru.ifmo.zakharvoit.extratask1.images;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class ImagesResultReceiver extends ResultReceiver {
    public static final int FINISHED = 0;
    public static final int LIST_DOWNLOADED = 1;
    public static final int IMAGE_DOWNLOADED = 2;
    public static final int ERROR = 3;

    public static final String IMAGE_BUNDLE_KEY = "image";
    public static final String SIZE_BUNDLE_KEY = "size";

    public static abstract class Receiver {
        public abstract void onListDownload(int size);
        public abstract void onImageDownload(byte[] image);
        public abstract void onFinishDownload();
        public abstract void onError();

        private void onReceiveResult(int resultCode, Bundle data) {
            switch (resultCode) {
                case LIST_DOWNLOADED:
                    onListDownload(data.getInt(SIZE_BUNDLE_KEY));
                    break;
                case IMAGE_DOWNLOADED:
                    onImageDownload(data.getByteArray(IMAGE_BUNDLE_KEY));
                    break;
                case ERROR:
                    onError();
                    break;
                case FINISHED:
                    onFinishDownload();
                    break;
            }
        }
    }

    private Receiver receiver;

    public ImagesResultReceiver() {
        super(new Handler());
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}
