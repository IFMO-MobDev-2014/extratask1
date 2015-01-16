package lapics.sergeybudkov.ru.lapics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PicsDatabase {
    private final Context context;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private static final String NAME = "PicsDatabase";
    private static final int VERSION = 1;
    private static final String TABLE = "Pictures";
    public static final String ID = "_id";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String IMAGE = "big";
    public static final String DB_CREATE =
            "Create table " + TABLE + "(" +
                    ID + " integer key, " +
                    WIDTH + " integer, " +
                    HEIGHT + " integer," +
                    IMAGE + " pic" +
                    ");";

    public PicsDatabase(Context context) {
        this.context = context;
    }

    public void open() {
        mDBHelper = new DBHelper(context, NAME, null, VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }


    public Cursor getAllData() {
        return mDB.query(TABLE, null, null, null, null, null, null);
    }

    public List<Bitmap> getAllPicturesData() {
        List<Bitmap> list = new ArrayList<Bitmap>();
        Cursor cursor = getAllData();
        while (cursor.moveToNext()) {
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            list.add(bitmap);
        }
        return list;
    }

    public void addChannel(SinglePicture picture) {
        ContentValues cv = new ContentValues();
        cv.put(WIDTH, picture.getWidth());
        cv.put(HEIGHT, picture.getHeight());
        cv.put(IMAGE, decodeBitmap(picture.getBigImage()));
        mDB.insert(TABLE, null, cv);
    }

    public Bitmap getPicture(int index) {
        ArrayList<Bitmap> arrayList = (ArrayList<Bitmap>) getAllPicturesData();
        return arrayList.get(index);
    }

    private byte[] decodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void deleteAllChannels() {
        mDB.delete(TABLE, null, null);
    }

}