package com.photofinder;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class MySwipeRefresh extends SwipeRefreshLayout {

    private int touchSlop;
    private float oldX;
    public MySwipeRefresh(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = MotionEvent.obtain(motionEvent).getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchSlop < Math.abs(oldX - motionEvent.getX())) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    public boolean canChildScrollUp() {
        return findViewById(R.id.gridView).canScrollVertically(-1);
    }
}
