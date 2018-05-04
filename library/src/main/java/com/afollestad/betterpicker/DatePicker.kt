package com.afollestad.betterpicker

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.afollestad.betterpicker.adapters.MonthColumnAdapter
import com.afollestad.betterpicker.adapters.NumberColumnAdapter
import com.afollestad.betterpicker.base.BasePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** A [BasePicker] which allows the selection of a month/day/year. */
class DatePicker(
  context: Context,
  attrs: AttributeSet
) : BasePicker(context, attrs) {

  companion object {
    private const val COLUMN_MONTH = 0
    private const val COLUMN_DAY = 1
    private const val COLUMN_YEAR = 2
  }

  private val currentDate = Calendar.getInstance()
  private val monthDateFormat: SimpleDateFormat
  private lateinit var daysOfMonthAdapter: NumberColumnAdapter

  init {
    val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker)
    try {
      var pickerMonthFormat =
        attributesArray.getString(R.styleable.DatePicker_pickerMonthFormat)
      if (pickerMonthFormat == null) {
        pickerMonthFormat = "MMM"
      }
      this.monthDateFormat = SimpleDateFormat(pickerMonthFormat, Locale.getDefault())
    } finally {
      attributesArray.recycle()
    }
  }

  override fun onShouldAddColumns() {
    addPickerColumn(
        MonthColumnAdapter(
            cellTextAppearance = cellTextAppearance,
            cellPadding = cellPadding
        ),
        selectionChange = { invalidateDaysOfMonth() }
    )

    val daysInThisMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    this.daysOfMonthAdapter = NumberColumnAdapter(
        max = daysInThisMonth,
        pad = true,
        cellTextAppearance = cellTextAppearance,
        cellPadding = cellPadding
    )
    addPickerColumn(this.daysOfMonthAdapter)

    val currentYear = currentDate.get(Calendar.YEAR)
    addPickerColumn(
        NumberColumnAdapter(
            min = 1990,
            max = currentYear,
            pad = false,
            cellTextAppearance = cellTextAppearance,
            cellPadding = cellPadding
        )
    )
  }

  override fun onReadyForInitialization() {
    setDisplayedDate()
  }

  /**
   * Sets the current date, doesn't immediately reflect in the UI unless this is called before the
   * view is attached to the window.
   */
  fun setCurrentDate(calendar: Calendar) {
    currentDate.time = calendar.time
  }

  /** @return currently selected time */
  fun getDate(): Calendar {
    val displayedTime = Calendar.getInstance()
    with(displayedTime) {
      time = monthDateFormat.parse(getSelectedValueInColumn(COLUMN_MONTH))
      set(Calendar.DAY_OF_MONTH, getSelectedValueInColumn(COLUMN_DAY))
      set(Calendar.YEAR, getSelectedValueInColumn(COLUMN_YEAR))
    }
    return displayedTime
  }

  /** Updates the UI to reflect the [currentDate] */
  private fun setDisplayedDate() {
    selectValueInColumn(COLUMN_MONTH, monthDateFormat.format(currentDate.time))
    selectValueInColumn(COLUMN_DAY, currentDate.get(Calendar.DAY_OF_MONTH))
    selectValueInColumn(COLUMN_YEAR, currentDate.get(Calendar.YEAR))
  }

  private fun invalidateDaysOfMonth() {
    val displayedDate = Calendar.getInstance()
    with(displayedDate) {
      set(Calendar.YEAR, getSelectedValueInColumn(COLUMN_YEAR))
      set(Calendar.DAY_OF_MONTH, COLUMN_DAY)
      set(Calendar.MONTH, getSelectedValueInColumn<String>(COLUMN_MONTH).monthId())
    }
    val daysInMonth = displayedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    this.daysOfMonthAdapter.updateMax(daysInMonth)
  }
}
