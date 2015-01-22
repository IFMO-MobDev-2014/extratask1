package lesson8.android.extratask1

import java.io.File
import java.lang.ref.WeakReference
import java.net.URL
import java.util

import android.app.LoaderManager.LoaderCallbacks
import android.app._
import android.content.{AsyncTaskLoader, Loader}
import android.os._
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.{Menu, MenuItem, ViewGroup}
import android.widget.Toast

import org.json._
import scalaj.http.{Http, HttpResponse}

object PhotoGalleryActivity {
  val loadFromDisc = 0
  val loadFromNet = 1
}

class PhotoGalleryActivity extends Activity with LoaderCallbacks[Album] {
  private var mAxisX: Int = 3
  private var mAxisY: Int = 4
  private var mViewPager: ViewPager = null
  private var mViewPagerAdapter: GridPagerAdapter = null
  private var mAlbum: Album = List()
  private var mFolder: File = null
  private var mDatabaseHelper: DatabaseHelper = null

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo_gallery)
    mViewPager = cast(findViewById(R.id.pager))
    mViewPagerAdapter = new GridPagerAdapter(getFragmentManager)
    mViewPager.setOffscreenPageLimit(6)
    mViewPager.setAdapter(mViewPagerAdapter)
    Log.d(this.toString, "onCreate in main activity ended")
    mFolder = getFilesDir
    mDatabaseHelper = new DatabaseHelper(this)
    getLoaderManager.initLoader(PhotoGalleryActivity.loadFromDisc, null, this).forceLoad()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {getMenuInflater.inflate(R.menu.menu_photo_gallery, menu); true}
  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case R.id.action_settings =>
      Toast.makeText(this, "Settings chosen", Toast.LENGTH_SHORT).show()
      Toast.makeText(this, "Settings chosen2", Toast.LENGTH_SHORT).show()
      true
    case R.id.action_refresh =>
      getLoaderManager.restartLoader(PhotoGalleryActivity.loadFromNet, null, this).forceLoad()
      Toast.makeText(this, "Updating", Toast.LENGTH_SHORT).show()
      true
  }

  class GridPagerAdapter(manager: FragmentManager) extends FragmentStatePagerAdapter(manager) {
    private val mFragmentMap: util.HashMap[Int, WeakReference[PhotoGalleryFragment]] = new util.HashMap[Int, WeakReference[PhotoGalleryFragment]]()
    override def getItem(position: Int): Fragment = {
      PhotoGalleryFragment.newInstance(mAxisX, mAxisY, mAlbum.grouped(mAxisX * mAxisY).toList(position), position)
    }
    override def getCount: Int = mAlbum.grouped(mAxisX * mAxisY).length
    override def destroyItem(container: ViewGroup, position: Int, `object`: scala.Any): Unit = {
      try {
        super.destroyItem(container, position, `object`)
        mFragmentMap.remove(position)
      } catch {
        case a: IllegalStateException => ()
      }
    }
    override def instantiateItem(container: ViewGroup, position: Int): PhotoGalleryFragment = {
      val ret: PhotoGalleryFragment = cast(super.instantiateItem(container, position))
      mFragmentMap.put(position, cast(new WeakReference[PhotoGalleryFragment](ret)))
      ret
    }
    def findRef(id: Int): Fragment = if (mFragmentMap.get(id) != null) mFragmentMap.get(id).get() else null
  }

  override def onCreateLoader(mode: Int, p2: Bundle): Loader[Album] = mode match {
    case PhotoGalleryActivity.loadFromDisc =>
      new AsyncTaskLoader[Album](this) {
        override def loadInBackground(): Album = mDatabaseHelper.mWrapper.getAlbum
      }
    case PhotoGalleryActivity.loadFromNet => new AsyncTaskLoader[Album](this) {
        override def loadInBackground(): Album = {
          val response: HttpResponse[String] =
            Http("http://api-fotki.yandex.ru/api/top/published/?format=json&limit=43").timeout(10000, 10000).asString
          val jsonObj = new JSONObject(response.body)
          val photos: JSONArray = jsonObj.getJSONArray("entries")
          var ret: List[Photo] = Nil
          Log.d(this.toString, photos.length().toString)
          for (i <- 0 until photos.length()) {
            val photo = photos.getJSONObject(i)
            val photoUrls = photo.optJSONObject("img")
            ret = new Photo(photo.getString("id"),
              new URL(photoUrls.getJSONObject("M").getString("href")),
              new URL(
//                if (!photo.getBoolean("hideOriginal"))
//                photoUrls.getJSONObject("orig").getString("href")
//              else
                photoUrls.getJSONObject("XXL").getString("href")
              )) :: ret
          }
          ret.foreach(println)
          ret
        }
      }
  }

  override def onLoaderReset(p1: Loader[Album]): Unit = ()

  override def onLoadFinished(p1: Loader[Album], p2: Album): Unit = {
    p1.getId match {
      case PhotoGalleryActivity.loadFromDisc => p2 match {
        case Nil => getLoaderManager.restartLoader(PhotoGalleryActivity.loadFromNet, null, this).forceLoad()
        case _ => mAlbum = p2
      }
      case PhotoGalleryActivity.loadFromNet => { mDatabaseHelper.mWrapper.putAlbum(p2); mAlbum = (p2 ::: mAlbum).distinct }
    }
    mViewPagerAdapter.notifyDataSetChanged()
  }
}