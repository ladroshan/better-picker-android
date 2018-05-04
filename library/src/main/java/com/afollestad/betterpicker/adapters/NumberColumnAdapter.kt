package com.afollestad.betterpicker.adapters

import com.afollestad.betterpicker.base.BaseColumnAdapter
import com.afollestad.betterpicker.base.BasePicker
import java.lang.String.format
import java.util.Locale

/**
 * This adapter manages a list of numbers, and allows the max to be updated dynamically.
 *
 * @param pageCount The number of pages. Anymore more than one progresses towards infinite scrolling. Each page will duplicate the previous.
 * @param min The number that appears at the top of the list.
 * @param max The number that appears at the bottom of the list.
 * @param pad Whether or not to pad the numbers. For an example, "1" becomes "01".
 * @param cellTextAppearance The text appearance applied to the TextView of each cell.
 * @param cellPadding The padding applied to all sides of the TextView of each cell.
 *
 * @author Aidan Follestad (afollestad)
 */
class NumberColumnAdapter(
  pageCount: Int = BasePicker.INFINITE_SCROLL_PAGE_COUNT,
  private val min: Int = 1,
  private val max: Int,
  private val pad: Boolean = false,
  cellTextAppearance: Int,
  cellPadding: Int
) :
    BaseColumnAdapter<Int>(pageCount, cellTextAppearance, cellPadding) {

  override fun initialData(): MutableList<Int> {
    val numbersList = mutableListOf<Int>()
    for (i in min..max) {
      numbersList.add(i)
    }
    return numbersList
  }

  override fun emptyValue(): Int {
    return -1
  }

  override fun displayValue(preprocessed: Int): String {
    return if (pad) format(Locale.US, "%02d", preprocessed) else preprocessed.toString()
  }

  /**
   * Updates the max number contained in this adapter, whether it's higher or lower than the current.
   */
  fun updateMax(newMax: Int) {
    if (data == null) {
      return
    }
    if (data!!.size < newMax) {
      // We need to push new items to the end
      for (i in data!!.size + 1..newMax) {
        data!!.add(i)
        notifyInsertions(data!!.size - 1)
      }
    } else if (data!!.size > newMax) {
      // We need to pop items off the end
      for (i in data!!.size - 1 downTo newMax) {
        data!!.removeAt(i)
        notifyRemovals(i)
      }
    }
  }

  private fun notifyInsertions(position: Int) {
    aliasPositions(position, { notifyItemInserted(it) })
  }

  private fun notifyRemovals(position: Int) {
    aliasPositions(position, { notifyItemRemoved(it) })
  }
}
