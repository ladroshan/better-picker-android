package com.afollestad.betterpicker.adapters

import com.afollestad.betterpicker.base.BaseColumnAdapter

/** This adapter manages a simple list of "AM" and "PM". ¬*/
internal class AmPmColumnAdapter(
  cellTextAppearance: Int,
  cellPadding: Int
) :
    BaseColumnAdapter<String>(1, cellTextAppearance, cellPadding) {

  override fun initialData(): MutableList<String> {
    return mutableListOf("AM", "PM")
  }

  override fun emptyValue(): String {
    return ""
  }

  override fun displayValue(preprocessed: String): String {
    return preprocessed
  }
}
