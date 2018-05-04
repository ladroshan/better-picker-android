package com.afollestad.betterpicker

import android.content.Context
import android.util.AttributeSet
import com.afollestad.betterpicker.adapters.AmPmColumnAdapter
import com.afollestad.betterpicker.adapters.NumberColumnAdapter
import com.afollestad.betterpicker.base.BasePicker
import java.util.Calendar

/** A [BasePicker] which allows the selection of hours/minutes and AM/PM */
class TimePicker(
  context: Context,
  attrs: AttributeSet
) : BasePicker(context, attrs) {

  companion object {
    private const val COLUMN_HOUR = 0
    private const val COLUMN_MINUTE = 1
    private const val COLUMN_AM_PM = 2
  }

  private val currentTime = Calendar.getInstance()

  override fun onShouldAddColumns() {
    addPickerColumn(
        NumberColumnAdapter(
            max = 12,
            pad = false,
            cellTextAppearance = cellTextAppearance,
            cellPadding = cellPadding
        )
    )
    addPickerColumn(
        NumberColumnAdapter(
            min = 0,
            max = 59,
            pad = true,
            cellTextAppearance = cellTextAppearance,
            cellPadding = cellPadding
        )
    )
    addPickerColumn(
        AmPmColumnAdapter(
            cellTextAppearance = cellTextAppearance,
            cellPadding = cellPadding
        )
    )
  }

  override fun onReadyForInitialization() {
    setDisplayedTime()
  }

  /** Updates the UI to reflect the [currentTime] */
  private fun setDisplayedTime() {
    selectValueInColumn(COLUMN_HOUR, currentTime.get(Calendar.HOUR))
    selectValueInColumn(COLUMN_MINUTE, currentTime.get(Calendar.MINUTE))
    val amPm = currentTime.get(Calendar.AM_PM)
    selectValueInColumn(COLUMN_AM_PM, amPm.amPmDisplay())
  }

  /**
   * Sets the current time, doesn't immediately reflect in the UI unless this is called before the
   * view is attached to the window.
   */
  fun setCurrentTime(calendar: Calendar) {
    currentTime.time = calendar.time
  }

  /** @return currently selected time */
  fun getTime(): Calendar {
    val displayedTime = Calendar.getInstance()
    // Noon and midnight are represented by 0, not by 12, so setting hour to 12 will cause the time
    // to roll over +12 hours from intended, which causes AM/PM to be set incorrectly.
    // Since we are setting AM/PM explicitly, we should convert the 12th hour to 0.
    // {@link https://developer.android.com/reference/java/util/Calendar.html#HOUR}
    displayedTime.set(Calendar.HOUR, getSelectedValueInColumn<Int>(COLUMN_HOUR) % 12)
    displayedTime.set(Calendar.MINUTE, getSelectedValueInColumn(COLUMN_MINUTE))
    val amPm = getSelectedValueInColumn<String>(COLUMN_AM_PM)
    displayedTime.set(Calendar.AM_PM, amPm.amPmId())
    return displayedTime
  }
}
