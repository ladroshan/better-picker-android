package com.afollestad.betterpicker.adapters

import com.afollestad.betterpicker.base.BaseColumnAdapter
import java.lang.String.format
import java.util.Locale

/**
 * Based on DataAdapter from register-android, I did not originally write this class.
 *
 * This adapter manages a list of numbers, and allows the max to be updated dynamically.
 */
internal class NumberColumnAdapter(
  private val min: Int = 1,
  private val max: Int,
  private val pad: Boolean = false,
  cellTextAppearance: Int,
  cellPadding: Int
) :
    BaseColumnAdapter<Int>(1, cellTextAppearance, cellPadding) {

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
        notifyItemInserted(data!!.size)
      }
    } else if (data!!.size > newMax) {
      // We need to pop items off the end
      for (i in data!!.size - 1 downTo newMax) {
        data!!.removeAt(i)
        notifyItemRemoved(i + 1)
      }
    }
  }
}
