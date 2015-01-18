package ru.ifmo.md.photoclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Шолохов on 16.01.2015.
 */
public class MyResultReceiver extends ResultReceiver {
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */

    public static final int DONE = 2;
    public static final  int PROGRESS = 1;
    public static final int FAIL = 0;

    private Receiver mReceiver;

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle data);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public MyResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
