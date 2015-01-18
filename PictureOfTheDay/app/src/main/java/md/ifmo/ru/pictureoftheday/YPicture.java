package md.ifmo.ru.pictureoftheday;

import android.graphics.Bitmap;

/**
 * Created by Илья on 17.01.2015.
 */
public class YPicture {
    public Bitmap bitmap;
    public String hrLink;
    public String pageLink;
    public String title;
    public YPicture(Bitmap bitmap, String link, String pageLink, String title) {
        this.bitmap = bitmap;
        this.hrLink = link;
        this.pageLink = pageLink;
        this.title = title;

    }
}
