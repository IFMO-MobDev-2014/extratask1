package md.ifmo.ru.pictureoftheday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Илья on 17.01.2015.
 */
public class DBPictures extends SQLiteOpenHelper {
    private static final String DB_NAME = "pictures_db";
    private static final int VERSION = 1;

    public static final String TABLE_PICTURES = "pictures";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PICTURE = "picture";
    public static final String COLUMN_PICTURE_HR = "picturehr";
    public static final String COLUMN_PICTURE_LINK = "link";
    public static final String COLUMN_PICTURE_TITLE = "title";

    private static final String INIT_PICTURES_TABLE =
            "CREATE TABLE " + TABLE_PICTURES + " (" +
                    COLUMN_ID + " INTEGER " + "PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PICTURE + " BLOB, " +
                    COLUMN_PICTURE_HR + " TEXT, " +
                    COLUMN_PICTURE_LINK + " TEXT, " +
                    COLUMN_PICTURE_TITLE + " TEXT );";

    public DBPictures(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(INIT_PICTURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXIST " + TABLE_PICTURES);
        onCreate(db);
    }
}
