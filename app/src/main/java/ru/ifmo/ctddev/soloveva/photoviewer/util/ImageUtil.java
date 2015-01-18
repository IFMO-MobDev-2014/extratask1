package ru.ifmo.ctddev.soloveva.photoviewer.util;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by maria on 17.01.15.
 */
public class ImageUtil {
    public static Drawable createDummyDrawable(int width, int height) {
        ShapeDrawable dummyImage = new ShapeDrawable(new RectShape());
        dummyImage.setIntrinsicWidth(width);
        dummyImage.setIntrinsicHeight(height);
        return dummyImage;
    }

    private static final Drawable squareDummy = createDummyDrawable(1, 1);

    public static Drawable getSquareDummy() {
        return squareDummy;
    }
}
