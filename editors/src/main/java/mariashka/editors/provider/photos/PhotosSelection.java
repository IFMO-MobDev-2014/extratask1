package mariashka.editors.provider.photos;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import mariashka.editors.provider.base.AbstractSelection;

/**
 * Selection for the {@code photos} table.
 */
public class PhotosSelection extends AbstractSelection<PhotosSelection> {
    @Override
    public Uri uri() {
        return PhotosColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code PhotosCursor} object, which is positioned before the first entry, or null.
     */
    public PhotosCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new PhotosCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null}.
     */
    public PhotosCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null}.
     */
    public PhotosCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public PhotosSelection id(long... value) {
        addEquals("photos." + PhotosColumns._ID, toObjectArray(value));
        return this;
    }


    public PhotosSelection name(String... value) {
        addEquals(PhotosColumns.NAME, value);
        return this;
    }

    public PhotosSelection nameNot(String... value) {
        addNotEquals(PhotosColumns.NAME, value);
        return this;
    }

    public PhotosSelection nameLike(String... value) {
        addLike(PhotosColumns.NAME, value);
        return this;
    }

    public PhotosSelection smallImg(byte[]... value) {
        addEquals(PhotosColumns.SMALL_IMG, value);
        return this;
    }

    public PhotosSelection smallImgNot(byte[]... value) {
        addNotEquals(PhotosColumns.SMALL_IMG, value);
        return this;
    }


    public PhotosSelection bigImg(byte[]... value) {
        addEquals(PhotosColumns.BIG_IMG, value);
        return this;
    }

    public PhotosSelection bigImgNot(byte[]... value) {
        addNotEquals(PhotosColumns.BIG_IMG, value);
        return this;
    }

}
