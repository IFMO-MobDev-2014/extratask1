package lesson8.android.extratask1

import android.content.{ContentResolver, Context}
import android.database.Cursor
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.provider.BaseColumns
import android.util.Log

object DatabaseHelper extends BaseColumns {
  private val DATABASE_VERSION: Int = 1
  val DATABASE_NAME = "pictures.db"
  val PHOTOS_TABLE_NAME = "pictures"
  val PHOTO_ID = "picture_id"
  val PHOTO_THUMBNAIL_URL = "picture_thumbnail_url"
  val PHOTO_FULLSIZE_URL = "picture_fullsize_url"
  val PHOTO_THUMBNAIL_FILE = "picture_thumbnail_file"
  val PHOTO_FULLSIZE_FILE = "picture_fullsize_file"

  val CREATE_PHOTOS_TABLE = "create table " +
    PHOTOS_TABLE_NAME + " (" +
    BaseColumns._ID + " integer primary key autoincrement, " +
    PHOTO_ID + " text not null, " +
    PHOTO_THUMBNAIL_URL + " text not null, " +
    PHOTO_FULLSIZE_URL + " text not null, " +
    PHOTO_THUMBNAIL_FILE + " text, " +
    PHOTO_FULLSIZE_FILE + " text);"
}

class DatabaseHelper(context: Context) extends SQLiteOpenHelper(context, DatabaseHelper.DATABASE_NAME, null, 1) with BaseColumns {
  val mWrapper = new HelperWrapper(context.getContentResolver)
  import lesson8.android.extratask1.DatabaseHelper._

  override def onCreate(db: SQLiteDatabase): Unit = {
    db.execSQL(CREATE_PHOTOS_TABLE)
    Log.d(this.toString, "created")
  }

  override def onUpgrade(p1: SQLiteDatabase, p2: Int, p3: Int): Unit = throw new UnsupportedOperationException("CANNOT UPGRADE DB")
}

class HelperWrapper(mContentResolver: ContentResolver) {
  def putPhoto(photo: Photo) = mContentResolver.insert(PhotoProvider.MAIN_CONTENT_URI, photo.getValues)
  def putAlbum(album: Album) = album.foreach(putPhoto)
  def getAlbum: Album = moveAndCompose(mContentResolver.query(PhotoProvider.MAIN_CONTENT_URI, null, null, null, null), Photo.fromCursor)
  def getAlbumByIDs(ids: List[String]): Album = moveAndCompose(
    mContentResolver.query(
      PhotoProvider.MAIN_CONTENT_URI,
      null,
      ids.map(DatabaseHelper.PHOTO_ID.toString + "='" + _ + "'").mkString(" OR "),
      null, null),
    Photo.fromCursor)
  def updatePhoto(photo: Photo) = mContentResolver.update(PhotoProvider.MAIN_CONTENT_URI,
    photo.getValues,
    DatabaseHelper.PHOTO_ID + "='" + photo.id + "'",
    null)

  private def moveAndCompose[A](cursor: Cursor, foo: (Cursor) => A): List[A] = {
    cursor.moveToFirst()
    compose(cursor, foo)
  }

  private def compose[A](cursor: Cursor, foo: (Cursor) => A): List[A] =
    if (cursor.isAfterLast) {cursor.close(); Nil}
    else foo(cursor) :: compose({
      cursor.moveToNext()
      cursor
    }, foo)
}