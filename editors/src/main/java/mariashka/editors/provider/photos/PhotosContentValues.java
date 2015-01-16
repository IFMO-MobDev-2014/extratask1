package mariashka.editors.provider.photos;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;

import mariashka.editors.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code photos} table.
 */
public class PhotosContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return PhotosColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, PhotosSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public PhotosContentValues putName(String value) {
        mContentValues.put(PhotosColumns.NAME, value);
        return this;
    }

    public PhotosContentValues putNameNull() {
        mContentValues.putNull(PhotosColumns.NAME);
        return this;
    }


    public PhotosContentValues putSmallImg(byte[] value) {
        mContentValues.put(PhotosColumns.SMALL_IMG, value);
        return this;
    }

    public PhotosContentValues putSmallImgNull() {
        mContentValues.putNull(PhotosColumns.SMALL_IMG);
        return this;
    }


    public PhotosContentValues putBigImg(byte[] value) {
        mContentValues.put(PhotosColumns.BIG_IMG, value);
        return this;
    }

    public PhotosContentValues putBigImgNull() {
        mContentValues.putNull(PhotosColumns.BIG_IMG);
        return this;
    }


    public PhotosContentValues putDescr(String value) {
        mContentValues.put(PhotosColumns.DESCR, value);
        return this;
    }

    public PhotosContentValues putDescrNull() {
        mContentValues.putNull(PhotosColumns.DESCR);
        return this;
    }


    public PhotosContentValues putCountry(String value) {
        mContentValues.put(PhotosColumns.COUNTRY, value);
        return this;
    }

    public PhotosContentValues putCountryNull() {
        mContentValues.putNull(PhotosColumns.COUNTRY);
        return this;
    }


    public PhotosContentValues putAuthor(String value) {
        mContentValues.put(PhotosColumns.AUTHOR, value);
        return this;
    }

    public PhotosContentValues putAuthorNull() {
        mContentValues.putNull(PhotosColumns.AUTHOR);
        return this;
    }


    public PhotosContentValues putFace(byte[] value) {
        mContentValues.put(PhotosColumns.FACE, value);
        return this;
    }

    public PhotosContentValues putFaceNull() {
        mContentValues.putNull(PhotosColumns.FACE);
        return this;
    }

}
