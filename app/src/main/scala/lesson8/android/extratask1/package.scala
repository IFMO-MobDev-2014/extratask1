package lesson8.android

import android.view.View
import android.widget.TextView

package object extratask1 {
  def cast[A, B](x: A): B = x match {
    case a: B => a
    case _ => throw new ClassCastException
  }

  type Album = List[Photo]

  def setText(view: View, text: String): Unit = {
    if (view != null) cast[View, TextView](view).setText(text)
  }
}