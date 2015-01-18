package com.pokrasko.extratask1;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ImageResultReceiver extends ResultReceiver {
    public static final int OK = 0;
    public static final int PROGRESS = 1;
    public static final int ERROR = 2;

    public interface Receiver {
        public void onReceiveResult(int code, Bundle data);
    }
    private Receiver receiver;

    public ImageResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int code, Bundle bundle) {
        if (receiver != null) {
            receiver.onReceiveResult(code, bundle);
        }
    }
}
