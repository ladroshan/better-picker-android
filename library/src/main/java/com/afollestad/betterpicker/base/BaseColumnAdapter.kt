package com.afollestad.betterpicker.base

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.betterpicker.R.layout
import com.afollestad.betterpicker.base.BaseColumnAdapter.ViewHolder
import com.afollestad.betterpicker.inflate
import com.afollestad.betterpicker.setTextAppearanceCompat

/**
 * The base class for all column adapters, housing common logic and things that consuming apps
 * don't need to care about.
 *
 * @param pageCount The number of pages. Anymore more than one progresses towards infinite scrolling. Each page will duplicate the previous.
 * @param cellTextAppearance The text appearance applied to the TextView of each cell.
 * @param cellPadding The padding applied to all sides of the TextView of each cell.
 *
 * @author Aidan Follestad (afollestad)
 */
abstract class BaseColumnAdapter<IT>(
  private val pageCount: Int = 1,
  private val cellTextAppearance: Int,
  private val cellPadding: Int
) :
    RecyclerView.Adapter<ViewHolder>() {

  var data: MutableList<IT>? = null

  init {
    if (pageCount < 1) {
      throw IllegalArgumentException("Page count cannot be less than 1!")
    }
  }

  /** We don't initialize this inline above, because subclasses aren't ready with locals at that point. */
  internal fun onReady() {
    data = initialData()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val view: TextView = parent.inflate(layout.picker_item, parent)
    view.setTextAppearanceCompat(cellTextAppearance)
    view.setPadding(cellPadding, cellPadding, cellPadding, cellPadding)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    val valueToDisplay = valueForPosition(position)
    if (valueToDisplay == emptyValue()) {
      holder.item.text = ""
    } else {
      holder.item.text = displayValue(valueToDisplay)
    }
  }

  override fun getItemCount(): Int {
    if (data == null) return 0
    // Number of elements for all the pages, plus padding top and bottom.
    return data!!.size * pageCount + 2
  }

  fun positionOf(value: IT): Int {
    if (data == null) return -1
    return (1                              // padding at the top, empty value.
        + pageCount / 2 * data!!.size      // middle page start.
        + data!!.indexOf(value))           // position within a page.
  }

  fun valueForPosition(position: Int): IT {
    if (data == null || position == 0 || position == itemCount - 1) {
      return emptyValue() // First and last element are padding, empty values.
    }
    val index = (position - 1) % data!!.size
    return data!![index]
  }

  /**
   * Gets all alias positions in an "infinite scroll" data set. For an example:
   *
   * data           =  [ "1", "2", "3, "4", "5" ]
   * pageCount      =  5
   * forPosition    =  1
   * aliasPositions =  [ 1, 6, 11, 16, 21 ]
   *
   * @param cb A callback that will receive each alias position one after another.
   */
  fun aliasPositions(
    forPosition: Int,
    cb: (Int) -> (Unit)
  ) {
    if (data == null) return
    val dataSize = data!!.size
    for (i in forPosition..dataSize * pageCount step dataSize) cb(i + 1)
  }

  /** A value representing the empty state of an item the adapter holds, e.g. a blank string or 0. */
  abstract fun emptyValue(): IT

  /** The initial data that the adapter holds, only called once. */
  abstract fun initialData(): MutableList<IT>

  /** Transforms an adapter item to be displayed in the UI. */
  abstract fun displayValue(preprocessed: IT): String

  class ViewHolder(
    val item: TextView
  ) : RecyclerView.ViewHolder(item)
}
