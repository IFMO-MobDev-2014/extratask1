package shambala.md.ifmo.photoofthday;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by 107476 on 12.01.2015.
 */
public class ImagesReceiver extends ResultReceiver {


    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int PROGRESS = 2;
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle data);
    }

    private Receiver mReceiver;

    public ImagesReceiver(Handler handler) {
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