package com.afollestad.betterpicker

import android.graphics.Rect
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.Calendar

@Suppress("UNCHECKED_CAST")
internal fun <VT : View> View.inflate(@LayoutRes id: Int, parent: ViewGroup? = null): VT {
  return LayoutInflater.from(context)
      .inflate(id, parent, false) as VT
}

@Suppress("DEPRECATION")
internal fun TextView.setTextAppearanceCompat(@StyleRes id: Int) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    this.setTextAppearance(id)
  } else {
    this.setTextAppearance(context, id)
  }
}

internal fun TextView.getSpaceForText(
  text: String,
  padding: Int = 0,
  rect: Rect? = null
): Int {
  val actualRect = rect ?: Rect()
  paint.getTextBounds(text, 0, text.length, actualRect)
  val widthOrHeight = Math.max(actualRect.width(), actualRect.height())
  return widthOrHeight + padding
}

internal fun String.monthId(): Int {
  return when (this.toLowerCase()) {
    "jan" -> Calendar.JANUARY
    "feb" -> Calendar.FEBRUARY
    "mar" -> Calendar.MARCH
    "apr" -> Calendar.APRIL
    "may" -> Calendar.MAY
    "jun" -> Calendar.JUNE
    "jul" -> Calendar.JULY
    "aug" -> Calendar.AUGUST
    "sep" -> Calendar.SEPTEMBER
    "oct" -> Calendar.OCTOBER
    "nov" -> Calendar.NOVEMBER
    "dec" -> Calendar.DECEMBER
    else -> throw IllegalArgumentException("Unrecognized month name \"$this\"")
  }
}

internal fun Int.amPmDisplay(): String {
  return if (this == Calendar.AM) "AM" else "PM"
}

internal fun String.amPmId(): Int {
  return if (this == "AM") Calendar.AM else Calendar.PM
}