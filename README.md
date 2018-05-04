# Better Pickers for Android

<img src="https://raw.githubusercontent.com/afollestad/better-picker-android/master/art/showcase.png" width="250" />

---

Welcome! `NumberPicker`'s and views that use it (like `DatePicker`) can be stubborn. 
They are not very responsive, changing size requires decimal scales, and they're not terribly 
extensible. This project aims to help fix that and make implementing pickers easy and pleasant. 

---

# Time Picker

### Basics

The time picker shows a columns for hours (12-hour), minutes, and AM/PM.

```xml
<com.afollestad.betterpicker.TimePicker
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/time_picker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    />
```

### Styling

Quite a few attributes that can be set:

```xml
<com.afollestad.betterpicker.TimePicker
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/time_picker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:pickerCellPadding="@dimen/default_picker_item_padding"
    app:pickerDividerColor="@color/default_gray"
    app:pickerOverlayBackground="@android:color/black"
    app:pickerDividerHeight="@dimen/default_divider_height"
    app:pickerOverlayOpacity="0.8"
    app:pickerCellTextAppearance="@style/DefaultItemTextAppearance"
    />
```

For `pickerCellTextAppearance`, you pass a basic style such as this:

```xml
<style name="DefaultItemTextAppearance">
    <item name="android:textColor">@color/default_white</item>
    <item name="android:textSize">@dimen/default_text_size</item>
</style>
```

---

# Date Picker

### Basics

The date picker shows columns for month, day, and year.

```xml
<com.afollestad.betterpicker.DatePicker
    android:id="@+id/date_picker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    />
  ```

### Styling

Quite a few attributes that can be set:

```xml
<com.afollestad.betterpicker.TimePicker
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/time_picker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:pickerCellPadding="@dimen/default_picker_item_padding"
    app:pickerDividerColor="@color/default_gray"
    app:pickerOverlayBackground="@android:color/black"
    app:pickerDividerHeight="@dimen/default_divider_height"
    app:pickerOverlayOpacity="0.8"
    app:pickerCellTextAppearance="@style/DefaultItemTextAppearance"
    app:pickerMonthFormat="MMM"
    />
```

The available attributes are mostly the same, with the addition of `pickerMonthFormat`. It takes a
[standard date format value](https://developer.android.com/reference/java/text/SimpleDateFormat) --
for an example, if the month was July, "MM" would render "07", "MMM" would render "Jul", and "MMMM"
would render "July". The default is "MMM".

Again, for `pickerCellTextAppearance`, you pass a basic style such as this:
       
```xml
<style name="DefaultItemTextAppearance">
   <item name="android:textColor">@color/default_white</item>
   <item name="android:textSize">@dimen/default_text_size</item>
</style>
```

---

# Custom Pickers

This library aims to be very extensible. A picker is made up of columns, each column has its own adapter.
A column is really just a `RecyclerView` that snaps to positions while scrolling.

### Adapter

Take the `AmPmColumnAdapter` class as an example of an adapter. Notice it inherits from `BaseColumnAdapter`,
and specifies that it holds `String` objects.

```java
class AmPmColumnAdapter(
  cellTextAppearance: Int,
  cellPadding: Int
) :
    BaseColumnAdapter<String>(1, cellTextAppearance, cellPadding) {

  // Responsible for populating the adapter with data initially.
  override fun initialData(): MutableList<String> {
    return mutableListOf("AM", "PM")
  }

  // This adapter is a BaseColumnAdapter<String>, so you return the empty representation of type String.
  // This gets used when adding empty first and last positions as list scroll padding.
  override fun emptyValue(): String {
    return ""
  }

  // Responsible for transforming an adapter value before being displayed in the list to the user, if needed.
  override fun displayValue(preprocessed: String): String {
    return preprocessed
  }
}

```

### Picker

Since we show the `AmPmColumnAdapter`, take the `TimePicker` as a sample of a picker. Notice it
inherits from `BasePicker`.

```java
class TimePicker(
  context: Context,
  attrs: AttributeSet
) : BasePicker(context, attrs) {

  companion object {
    private const val COLUMN_HOUR = 0
    private const val COLUMN_MINUTE = 1
    private const val COLUMN_AM_PM = 2
  }

  // This gets instantiated with each class instantiation, it defaults to the current device time.
  private val currentTime = Calendar.getInstance()

  // The BasePicker has reported it's time to add columns.
  // You can see the AmPmColumnAdapter shown above in use, along with a couple NumberColumnAdapters.
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

  // The BasePicker has reported that the view is ready for any other required initialization before
  // being shown to the user. Here we invalidate the UI to show the current date.
  override fun onReadyForInitialization() {
    setDisplayedTime()
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
    displayedTime.set(Calendar.HOUR, getSelectedValueInColumn<Int>(COLUMN_HOUR) % 12)
    displayedTime.set(Calendar.MINUTE, getSelectedValueInColumn(COLUMN_MINUTE))
    val amPm = getSelectedValueInColumn<String>(COLUMN_AM_PM)
    // amPmId() is a internal extension method that converts "AM" to Calendar.AM or "PM" to Calendar.PM
    displayedTime.set(Calendar.AM_PM, amPm.amPmId())
    return displayedTime
  }

  /** Updates the UI to reflect the [currentTime] */
  private fun setDisplayedTime() {
    selectValueInColumn(COLUMN_HOUR, currentTime.get(Calendar.HOUR))
    selectValueInColumn(COLUMN_MINUTE, currentTime.get(Calendar.MINUTE))
    val amPm = currentTime.get(Calendar.AM_PM)
    // amPmDisplay() is a internal extension method that converts Calendar.AM to "AM" or Calendar.PM to "PM"
    selectValueInColumn(COLUMN_AM_PM, amPm.amPmDisplay())
  }
}
```