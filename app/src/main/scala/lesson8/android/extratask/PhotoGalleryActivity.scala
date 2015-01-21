package lesson8.android.extratask

import android.app._
import android.os._
import android.view.{MenuItem, Menu}
import android.widget.Toast

class PhotoGalleryActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo_gallery)
  }
  override def onCreateOptionsMenu(menu: Menu): Boolean = {getMenuInflater.inflate(R.menu.menu_photo_gallery, menu); true}
  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case R.id.action_settings =>
      Toast.makeText(this, "Settings chosen", Toast.LENGTH_SHORT).show()
      Toast.makeText(this, "Settings chosen2", Toast.LENGTH_SHORT).show()
      true
  }
}