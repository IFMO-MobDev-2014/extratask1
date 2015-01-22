package lesson8.android.extratask1

import java.io.File

import android.app.LoaderManager.LoaderCallbacks
import android.app.{Activity, Fragment}
import android.content.{AsyncTaskLoader, Context, Intent, Loader}
import android.os.Bundle
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.util.Log
import android.view.View.OnClickListener
import android.view.{Gravity, LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ProgressBar, RelativeLayout}

object PhotoGalleryFragment {
  val loadFromDB = 0
  val loadThumbnails = 1

  val axisX = "axisX"
  val axisY = "axisY"
  val photosList = "photosList"
  val position = "position"
  def newInstance(x: Int, y: Int, photos: List[Photo], position: Int): PhotoGalleryFragment = {
    val bundle: Bundle = new Bundle()
    bundle.putInt(axisX, x)
    bundle.putInt(axisY, y)
    bundle.putStringArray(photosList, photos.map(_.id).toArray)
    bundle.putInt(PhotoGalleryFragment.position, position)
    val fragment = new PhotoGalleryFragment
    fragment.setArguments(bundle)
    fragment
  }
}

class PhotoGalleryFragment extends Fragment with LoaderCallbacks[Album] {
  private var (m, n) = (3, 4)
  private var mDatabaseHelper: DatabaseHelper = null
  private var mPhotosIDs: List[String] = Nil
  private var mAlbum: Album = Nil
  private var mRecyclerView: RecyclerView = null
  private var mRecyclerViewManager: GridLayoutManager = null
  private var mRecyclerViewAdapter: GridRecyclerViewAdapter = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    import lesson8.android.extratask1.PhotoGalleryFragment._
    m = getArguments.getInt(axisX)
    n = getArguments.getInt(axisY)
    mRecyclerViewManager = new GridLayoutManager(getActivity, m)
    mRecyclerViewManager.setSpanCount(m)
    mPhotosIDs = getArguments.getStringArray(photosList).toList
    mRecyclerViewAdapter = new GridRecyclerViewAdapter
    getLoaderManager.initLoader(loadFromDB, null, this).forceLoad()
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    mDatabaseHelper = new DatabaseHelper(activity)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val ret = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    mRecyclerView = cast(ret.findViewById(R.id.photo_recycler_grid))
    mRecyclerView.setHasFixedSize(true)
    mRecyclerView.setHorizontalScrollBarEnabled(false)
    mRecyclerView.setHorizontalFadingEdgeEnabled(false)
    if (mRecyclerViewManager != null) mRecyclerView.setLayoutManager(mRecyclerViewManager)
    if (mRecyclerViewAdapter != null) {
      mRecyclerView.setAdapter(mRecyclerViewAdapter)
    }
    ret
  }

  override def onPause(): Unit = super.onPause()

  override def onCreateLoader(mode: Int, p2: Bundle): Loader[Album] = mode match {
    case PhotoGalleryFragment.loadFromDB => new AsyncTaskLoader[Album](getActivity) {
      override def loadInBackground(): Album = mDatabaseHelper.mWrapper.getAlbumByIDs(mPhotosIDs)
    }
    case PhotoGalleryFragment.loadThumbnails => new AsyncTaskLoader[Album](getActivity) {
      // return list of all empty items in mAlbum with downloaded (potentially) bitmaps
      override def loadInBackground(): Album = mAlbum
        .filter(_.thumbnail match { case None => true; case _ => false})
        .map((ph: Photo) =>
         ph.downloadAndSaveThumbnail(getActivity.getFilesDir,
           (f: File, newph: Photo) => mAlbum.find(_.id == newph.id) match {
             case Some(b) =>
               Log.d(this.toString, "in callback - updating " + mAlbum.indexOf(b).toString)
               mAlbum = mAlbum.updated(mAlbum.indexOf(b), newph)
               mRecyclerViewAdapter.notifyDataSetChanged()
               mRecyclerViewAdapter.notifyItemChanged(mAlbum.indexOf(b))
               mDatabaseHelper.mWrapper.updatePhoto(newph)
             case _ => Log.e(this.toString, "WTF IN PHOTO CALLBACK")
           }
         ))
    }
  }

  override def onLoaderReset(p1: Loader[Album]): Unit = ()

  override def onLoadFinished(p1: Loader[Album], p2: Album): Unit = p1.getId match {
    case PhotoGalleryFragment.loadFromDB => p2 match {
      case Nil => Log.e(this.toString, "empty album from DB in fragment")
      case _ =>
        mAlbum = p2
        mRecyclerViewAdapter.notifyDataSetChanged()
        getLoaderManager.initLoader(PhotoGalleryFragment.loadThumbnails, null, this).forceLoad()
    }
    case PhotoGalleryFragment.loadThumbnails =>
      Log.d(this.toString, "Loaded photos: " +
        mAlbum.count(_.thumbnail match { case None => false; case _ => true}) + " of " + mAlbum.length)
      mRecyclerViewAdapter.notifyDataSetChanged()
      if (mAlbum.exists(_.thumbnail match { case None => true; case _ => false})) {
        Log.d(this.toString, "Restarting photo download, there are some without thumbnail: N=" +
          mAlbum.count(_.thumbnail match { case None => true; case _ => false}))
        getLoaderManager.restartLoader(PhotoGalleryFragment.loadThumbnails, null, this)
      }
  }

  class GridRecyclerViewAdapter extends RecyclerView.Adapter[GridViewHolder] {
    private var context: Context = null

    def addImg(position: Int, view: SquareRelativeLayout): SquareRelativeLayout = {
      view.removeAllViews()
      view.setGravity(RelativeLayout.CENTER_IN_PARENT)
      mAlbum(position).getThumbnail match {
        case Some(a) =>
          val img = new ImageView(context)
          img.setImageBitmap(a)
          img.setScaleType(ImageView.ScaleType.CENTER)
          view.addView(img)
        //          view.setBackground(cast(new BitmapDrawable(a)))
        case None =>
          val bar = new ProgressBar(context)
          view.addView(bar)
      }
      view.setPadding(3,3,3,3)
      view.setGravity(Gravity.CENTER)
      view.setOnClickListener(new OnClickListener {
        override def onClick(p1: View): Unit = {
          val newIntent = new Intent(context, classOf[PhotoViewActivity])
          newIntent.putExtra(PhotoViewActivity.photoId, mAlbum(position).id)
          startActivity(newIntent)
        }
      })
      view
    }

    override def onCreateViewHolder(p1: ViewGroup, position: Int): GridViewHolder = {
      context = p1.getContext
      var view: SquareRelativeLayout = cast(LayoutInflater.from(context).inflate(R.layout.square_image, p1, false))
      view = addImg(position, view)
      new GridViewHolder(view)
    }
    override def getItemCount: Int = mAlbum.length
    override def onBindViewHolder(holder: GridViewHolder, pos: Int): Unit = {
//      Log.d(this.toString, "Setting image for fragment with " + mAlbum.length + " positions")
      holder.sq = addImg(pos, holder.sq)
    }
  }
  class GridViewHolder(var sq: SquareRelativeLayout) extends RecyclerView.ViewHolder(sq) {}
}

