package mariashka.editors;

/**
 * Created by mariashka on 1/16/15.
 */
public class PhotoItem {
    String name;
    byte[] smallImg;
    byte[] bigImg;
    String descr;
    String author;
    byte[] face;

    PhotoItem(String name, byte[] small, byte[] big){
        this.name = name;
        smallImg = small;
        bigImg = big;
    }
}
