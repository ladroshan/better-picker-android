package com.afollestad.betterpicker.base

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.betterpicker.R.layout
import com.afollestad.betterpicker.base.BaseColumnAdapter.ViewHolder

/** Based on DataAdapter from register-android. */
abstract class BaseColumnAdapter<IT>(
  private val pageCount: Int,
  val cellTextAppearance: Int,
  val cellPadding: Int
) :
    RecyclerView.Adapter<ViewHolder>() {

  var data: MutableList<IT>? = null

  /** We don't initialize this inline above, because subclasses aren't ready with locals at that point. */
  internal fun onReady() {
    data = initialData()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(layout.picker_item, parent, false) as TextView
    view.setTextAppearance(parent.context, cellTextAppearance)
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

  abstract fun emptyValue(): IT

  abstract fun initialData(): MutableList<IT>

  abstract fun displayValue(preprocessed: IT): String

  class ViewHolder(
    val item: TextView
  ) : RecyclerView.ViewHolder(item)
}
