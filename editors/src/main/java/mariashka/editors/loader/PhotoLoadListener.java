package mariashka.editors.loader;

import android.content.Loader;
import android.os.Bundle;

import java.util.List;

/**
 * Created by mariashka on 1/16/15.
 */
public interface PhotoLoadListener {
    void onLoadFinished(List<PhotoItem> data);
    void onCancelLoad(List<PhotoItem> data);
}
