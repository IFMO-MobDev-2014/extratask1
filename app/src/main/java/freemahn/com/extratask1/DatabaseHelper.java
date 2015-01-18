package freemahn.com.extratask1;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Freemahn on 17.01.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "entriesdatabase.db";
    public static final String DATABASE_TABLE = "entries";
    public static final int DATABASE_VERSION = 1;
    public static final String TITLE_COLUMN = "title";
    public static final String LINK_SMALL_COLUMN = "link_small";
    public static final String LINK_BIG_COLUMN = "link_big";
    public static final String IMAGE_COLUMN = "img";



    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + TITLE_COLUMN
            + " text not null, " + LINK_SMALL_COLUMN + " text not null, "+ LINK_BIG_COLUMN + " text not null, " + IMAGE_COLUMN + " blob); ";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + DATABASE_TABLE;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
