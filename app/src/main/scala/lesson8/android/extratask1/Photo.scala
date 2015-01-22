package lesson8.android.extratask1

import java.io.{File, FileOutputStream, InputStream}
import java.net.{HttpURLConnection, URL}

import android.content.ContentValues
import android.database.Cursor
import android.graphics.{Bitmap, BitmapFactory}
import android.util.Log

object Photo {
  def fromCursor(cursor: Cursor): Photo =
    new Photo(cursor.getString(1),
      new URL(cursor.getString(2)),
      new URL(cursor.getString(3)),
      if (cursor.getString(4) == "") None else Some(new File(cursor.getString(4))),
      if (cursor.getString(5) == "") None else Some(new File(cursor.getString(5))))
}

class Photo(val id: String,
            val thumbnailUrl: URL,
            val fullsizeUrl: URL,
            val thumbnail: Option[File] = None,
            val fullsize: Option[File] = None) {
  def getValues: ContentValues = {
    import lesson8.android.extratask1.DatabaseHelper._
    val values = new ContentValues
    values.put(PHOTO_ID, id)
    values.put(PHOTO_THUMBNAIL_URL, thumbnailUrl.toString)
    values.put(PHOTO_FULLSIZE_URL, fullsizeUrl.toString)
    values.put(PHOTO_THUMBNAIL_FILE, thumbnail match { case Some(a) => a.getAbsolutePath; case _ => ""})
    values.put(PHOTO_FULLSIZE_FILE, thumbnail match { case Some(a) => a.getAbsolutePath; case _ => ""})
    values
  }

  def getBitmap(file: Option[File]): Option[Bitmap] = file match {
    case None => None
    case Some(f) =>
      val options: BitmapFactory.Options = new BitmapFactory.Options()
      options.inPreferredConfig = Bitmap.Config.ARGB_8888
      Some(BitmapFactory.decodeFile(f.toString, options))
  }

  def getThumbnail: Option[Bitmap] = getBitmap(thumbnail)
  def getFullsize: Option[Bitmap] = getBitmap(fullsize)

  def loadFromURL(url: URL) =
    try {
      val connection: HttpURLConnection = cast(url.openConnection())
      connection.setDoInput(true)
      connection.connect()
      val input: InputStream = connection.getInputStream
      val bitmap: Bitmap = BitmapFactory.decodeStream(input)
      bitmap
    } catch {
      case e: Exception => null
    }
  // copypaste's awesome!
  def downloadAndSaveThumbnail(dir: File, callback: (File, Photo) => Unit) = {
    Log.d(this.toString, "Downloading photo")
    thumbnail match {
      case None => try {
        val path = new File(dir.getAbsolutePath + "/" + id + ".png")
        val out = new FileOutputStream(path)
        loadFromURL(thumbnailUrl).compress(Bitmap.CompressFormat.PNG, 100, out)
        Log.d(this.toString, "Downloaded and saved thumbnail into " + path.getAbsolutePath)
        val ret = new Photo(id, thumbnailUrl, fullsizeUrl, Some(path), fullsize)
        Log.d(this.toString, "Calling callback")
        callback(path, ret)
        ret
      } catch {
        case _: Throwable => this
      }
      case _ => this
    }
  }

  def downloadAndSaveFullsize(dir: File) = {
    Log.d(this.toString, "Downloading fullsize photo")
    fullsize match {
      case None => try {
        val path = new File(dir.getAbsolutePath + "/" + id + ".png")
        val out = new FileOutputStream(path)
        loadFromURL(thumbnailUrl).compress(Bitmap.CompressFormat.PNG, 100, out)
        Log.d(this.toString, "Downloaded and saved thumbnail into " + path.getAbsolutePath)
        val ret = new Photo(id, thumbnailUrl, fullsizeUrl, thumbnail, Some(path))
        ret
      } catch {
        case _: Throwable => this
      }
      case _ => this
    }
  }
}