package ru.ifmo.md.extratask1.photoclient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by sergey on 18.01.15.
 */
public class BroadcastStateSender {

    public static final String BROADCAST_ACTION = "ru.ifmo.md.extratask1.photoclient.BROADCAST_CLIENT";
    public static final String EXTRA_STATE_CODE = "broadcast_state_code";

    public static final int STATE_COMPLETE = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_NO_CONNECTION = 2;

    private LocalBroadcastManager mManager;

    public BroadcastStateSender(Context context) {
        this.mManager = LocalBroadcastManager.getInstance(context);
    }

    public void sendBroadcastState(int state) {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra(EXTRA_STATE_CODE, state);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        mManager.sendBroadcast(intent);
    }

}
