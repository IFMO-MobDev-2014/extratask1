package lesson8.android.extratask1

import android.app.LoaderManager.LoaderCallbacks
import android.app.{Activity, WallpaperManager}
import android.content.{AsyncTaskLoader, Loader}
import android.os.Bundle
import android.provider.MediaStore
import android.view.{Menu, MenuItem, View}
import android.widget.{ImageView, ProgressBar, Toast}

object PhotoViewActivity {
  val photoId = "photo_id"
}

class PhotoViewActivity extends Activity with LoaderCallbacks[Photo] {
  private var mPhoto: Photo = null
  private var mDatabaseHelper: DatabaseHelper = null
  private var mProgressBar: ProgressBar = null
  private var mImageView: ImageView = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo_view)
    mProgressBar = cast(findViewById(R.id.progress_bar))
    mImageView = cast(findViewById(R.id.image_detail_view))
    mDatabaseHelper = new DatabaseHelper(this)
    mPhoto = mDatabaseHelper.mWrapper.getAlbumByIDs(List(getIntent.getStringExtra(PhotoViewActivity.photoId)))(0)
    getLoaderManager.initLoader(0, null, this)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.menu_photo_view, menu); true
  }
  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case R.id.action_download =>
      mPhoto.getFullsize match {
        case None => Toast.makeText(this, "Wait for photo to download, please", Toast.LENGTH_SHORT).show()
        case Some(bitmap) =>
          MediaStore.Images.Media.insertImage(getContentResolver, bitmap, mPhoto.id, "saved from extratask1");
          Toast.makeText(this, "Saved to gallery", Toast.LENGTH_SHORT).show()
      }
      true
    case R.id.action_wallpaper_set =>
      mPhoto.getFullsize match {
        case None => Toast.makeText(this, "Not ready yet", Toast.LENGTH_SHORT).show()
        case Some(bitmap) =>
          val image: WallpaperManager = WallpaperManager.getInstance(getApplicationContext)
          image.setBitmap(bitmap)
      }
      true
    case _ => false
  }

  override def onCreateLoader(p1: Int, p2: Bundle): Loader[Photo] =
    new AsyncTaskLoader[Photo](this) {
      override def loadInBackground(): Photo = mPhoto.downloadAndSaveFullsize(getFilesDir)
    }
  override def onLoaderReset(p1: Loader[Photo]): Unit = ()
  override def onLoadFinished(p1: Loader[Photo], p2: Photo): Unit = {
    mProgressBar.setVisibility(View.GONE)
    p2.getFullsize match {
      case None => Toast.makeText(this, "Failed to load picture", Toast.LENGTH_SHORT)
      case Some(a) =>
        mPhoto = p2
        mImageView.setImageBitmap(a)
        mImageView.setVisibility(View.VISIBLE)
        mDatabaseHelper.mWrapper.updatePhoto(p2)
    }
  }
}