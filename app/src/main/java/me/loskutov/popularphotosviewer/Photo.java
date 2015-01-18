package me.loskutov.popularphotosviewer;

/**
 * Created by ignat on 16.01.15.
 */
class Photo {
    public final String preview;
    public final String large;
    public final String id;
    public final String orig;
    public final String url;

    Photo(String id, String preview, String large, String orig, String url) {
        this.id = id;
        this.preview = preview;
        this.large = large;
        this.orig = orig;
        this.url = url;
    }
}
