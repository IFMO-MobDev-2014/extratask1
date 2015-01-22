package ru.ifmo.md.extratask1;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


public class ProgressReceiver extends ResultReceiver {


    public static final int DONE = 0;
    public static final int ERROR = 1;
    public static final int IMGLOADED = 2;
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle data);
    }

    private Receiver mReceiver;

    public ProgressReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}