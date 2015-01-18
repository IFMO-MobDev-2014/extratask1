package freemahn.com.extratask1;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Freemahn on 16.01.2015.
 */
public class Entry {
    public String title;
    public String linkSmall;
    public String linkBig;
    Bitmap image;

    public Entry() {

    }
    public Entry(String title, String linkSmall, String linkBig) {
        this.title = title;
        this.linkSmall = linkSmall;
        this.linkBig = linkBig;
    }

    @Override
    public String toString() {
        return title + " " + "\n" + linkSmall + "\n" + linkBig;
    }
}
