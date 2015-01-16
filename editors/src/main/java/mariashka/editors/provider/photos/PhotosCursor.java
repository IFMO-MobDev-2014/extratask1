package mariashka.editors.provider.photos;

import java.util.Date;

import android.database.Cursor;

import mariashka.editors.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code photos} table.
 */
public class PhotosCursor extends AbstractCursor {
    public PhotosCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code name} value.
     * Can be {@code null}.
     */
    public String getName() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.NAME);
        return getString(index);
    }

    /**
     * Get the {@code small_img} value.
     * Can be {@code null}.
     */
    public byte[] getSmallImg() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.SMALL_IMG);
        return getBlob(index);
    }

    /**
     * Get the {@code big_img} value.
     * Can be {@code null}.
     */
    public byte[] getBigImg() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.BIG_IMG);
        return getBlob(index);
    }

    /**
     * Get the {@code descr} value.
     * Can be {@code null}.
     */
    public String getDescr() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.DESCR);
        return getString(index);
    }

    /**
     * Get the {@code country} value.
     * Can be {@code null}.
     */
    public String getCountry() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.COUNTRY);
        return getString(index);
    }

    /**
     * Get the {@code author} value.
     * Can be {@code null}.
     */
    public String getAuthor() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.AUTHOR);
        return getString(index);
    }

    /**
     * Get the {@code face} value.
     * Can be {@code null}.
     */
    public byte[] getFace() {
        Integer index = getCachedColumnIndexOrThrow(PhotosColumns.FACE);
        return getBlob(index);
    }
}
