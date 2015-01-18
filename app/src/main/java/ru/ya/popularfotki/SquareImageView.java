package ru.ya.popularfotki;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by vanya on 18.01.15.
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mx = Math.max(getMeasuredHeight(), getMeasuredWidth());
        //setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        setMeasuredDimension(mx, mx); //Snap to width
    }
}
