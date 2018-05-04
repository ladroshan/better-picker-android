package com.afollestad.betterpicker.adapters

import com.afollestad.betterpicker.base.BasePicker
import com.afollestad.betterpicker.base.BaseColumnAdapter
import com.afollestad.betterpicker.base.BasePicker.Companion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * This adapter manages a list of months in a year.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class MonthColumnAdapter(
  cellTextAppearance: Int,
  cellPadding: Int
) :
    BaseColumnAdapter<String>(
        BasePicker.INFINITE_SCROLL_PAGE_COUNT, cellTextAppearance, cellPadding
    ) {

  override fun initialData(): MutableList<String> {
    val monthDateFormat = SimpleDateFormat("MMM", Locale.getDefault())
    val monthNameList = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    for (i in 0..11) {
      calendar.set(Calendar.MONTH, i)
      monthNameList.add(monthDateFormat.format(calendar.time))
    }
    return monthNameList
  }

  override fun emptyValue(): String {
    return ""
  }

  override fun displayValue(preprocessed: String): String {
    return preprocessed
  }
}
