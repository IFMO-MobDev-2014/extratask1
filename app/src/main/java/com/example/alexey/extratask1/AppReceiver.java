package com.example.alexey.extratask1;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


public class AppReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public AppReceiver(Handler handler) {
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

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle data);
    }
}
