package mariashka.editors;

import java.util.List;

/**
 * Created by mariashka on 1/16/15.
 */
public interface PhotoLoadListener {
    void onLoadFinished(List<PhotoItem> data);
    void onCancelLoad(List<PhotoItem> data);
}
