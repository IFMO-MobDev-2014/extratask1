package md.ifmo.ru.pictureoftheday;

import android.graphics.Bitmap;

/**
 * Created by Илья on 17.01.2015.
 */
public class YPicture {
    public Bitmap bitmap;
    public byte[] bytebitmap;
    public String hrLink;
    public String pageLink;
    public String title;
    public YPicture(byte[] bytebitmap, String link, String pageLink, String title) {
        this.bytebitmap = bytebitmap;
        this.hrLink = link;
        this.pageLink = pageLink;
        this.title = title;

    }
}
