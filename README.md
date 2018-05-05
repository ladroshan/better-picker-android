# Better Pickers for Android

[![Build Status](https://travis-ci.org/afollestad/better-picker-android.svg)](https://travis-ci.org/afollestad/better-picker-android)
[ ![Download](https://api.bintray.com/packages/drummer-aidan/maven/betterpicker/images/download.svg) ](https://bintray.com/drummer-aidan/maven/betterpicker/_latestVersion)

<img src="https://raw.githubusercontent.com/afollestad/better-picker-android/master/art/showcase.png" width="250" />

---

Welcome! `NumberPicker`'s and views that use it (like `DatePicker`) can be stubborn. 
They are not very responsive, changing size requires decimal scales, and they're not terribly 
extensible. This project aims to help fix that and make implementing pickers easy and pleasant. 

1. [Gradle Dependency](https://github.com/afollestad/better-picker-android#gradle-dependency)
2. [Time Picker](https://github.com/afollestad/better-picker-android#time-picker)
    1. [Basics](https://github.com/afollestad/better-picker-android#basics)
    2. [Configuration](https://github.com/afollestad/better-picker-android#configuration)
    3. [Interaction](https://github.com/afollestad/better-picker-android#interaction)
3. [Date Picker](https://github.com/afollestad/better-picker-android#date-picker)
    1. [Basics](https://github.com/afollestad/better-picker-android#basics-1)
    2. [Configuration](https://github.com/afollestad/better-picker-android#configuration-1)
    3. [Interaction](https://github.com/afollestad/better-picker-android#interaction-1)
4. [Custom Pickers](https://github.com/afollestad/better-picker-android#custom-pickers)
    1. [Column Adapters](https://github.com/afollestad/better-picker-android#column-adapters)
    2. [Picker Views](https://github.com/afollestad/better-picker-android#picker-views)
5. [Advanced Topics](https://github.com/afollestad/better-picker-android#custom-pickers)
    1. [Infinite Scrolling](https://github.com/afollestad/better-picker-android#infinite-scrolling)
    2. [Listening for Selection Changes](https://github.com/afollestad/better-picker-android#listening-for-selection-changes)
    2. [Updating Datasets and Alias Positions](https://github.com/afollestad/better-picker-android#updating-data-sets-and-alias-positions)

---

# Gradle Dependency

```gradle
dependencies {
  ...
  implementation 'com.afollestad:betterpicker:0.1.3'
}
```

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

### Configuration

There are a few attributes that can be used in your layout:

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

### Interaction

The `TimePicker` has a few simple methods.

```kotlin
val newTime = GregorianCalendar(1995, 7, 28, 3, 15, 30)
// This updates the displayed and stored time in the picker.
// invalidateNow is an optional parameter, defaulting to false.
// Only need to pass true if you're calling this after the window is attached.
timePicker.setCurrentTime(newTime, invalidateNow = true)

// Returns a Calendar instance.
// You should only be concerned with the stored hour, minute, and AM/PM.
val displayedTime = timePicker.getTime()
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

### Configuration

Like the date picker, there are a few attributes you can set:

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

Again, for `pickerCellTextAppearance`, you pass a basic style such as this:

```xml
<style name="DefaultItemTextAppearance">
   <item name="android:textColor">@color/default_white</item>
   <item name="android:textSize">@dimen/default_text_size</item>
</style>
```

The available attributes are mostly the same, with the addition of `pickerMonthFormat`. It takes a
[standard date format value](https://developer.android.com/reference/java/text/SimpleDateFormat) --
for an example, if the month was July, "MM" would render "07", "MMM" would render "Jul", and "MMMM"
would render "July". The default is "MMM".

### Interaction

Like the `TimePicker`, there are a few simple methods in the `DatePicker`:

```kotlin
val newDate = GregorianCalendar(1995, 7, 28)
// This updates the displayed and stored date in the picker.
// invalidateNow is an optional parameter, defaulting to false.
// Only need to pass true if you're calling this after the window is attached.
datePicker.setCurrentDate(newDate, invalidateNow = true)

// Returns a Calendar instance.
// You should only be concerned with the stored month, day of month, and year.
val displayedDate = datePicker.getDate()
```

---

# Custom Pickers

This library aims to be very extensible. A picker is made up of columns, each column has its own adapter.
A column is really just a `RecyclerView` that snaps to positions while scrolling.

### Column Adapters

Take the `AmPmColumnAdapter` class as an example of a column adapter. Notice it inherits from `BaseColumnAdapter`,
and specifies that it holds `String` objects.

```kotlin
class AmPmColumnAdapter(
  cellTextAppearance: Int,
  cellPadding: Int
) : BaseColumnAdapter<String>(cellTextAppearance, cellPadding) {

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

In the constructor of `BaseColumnAdapter` here, we pass `cellTextAppearance` and `cellPadding`.
These both get applied directly to the `TextView` which makes up a cell (row) in a column.

### Picker Views

Since we show the `AmPmColumnAdapter` above, let's take the `TimePicker` as a sample of a picker.
Notice it inherits from `BasePicker`.

```kotlin
class TimePicker(
  context: Context,
  attrs: AttributeSet
) : BasePicker(context, attrs) {

  companion object {
    // Avoid magic numbers in your code, make them constants.
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

---

# Advanced Topics

### Infinite Scrolling

You'll notice the included `TimePicker` and `DatePicker` allow you to infinitely scroll through their values.
For an example, when you get to "December" in the date picker, you see "January" next in the list.

This is done using simple page counts in the `BaseColumnAdapter`. If you were to look in this library's
source code at `MonthColumnAdapter`, you'd see this in the class declaration:

```kotlin
internal class MonthColumnAdapter(cellTextAppearance: Int, cellPadding: Int)
    : BaseColumnAdapter<String>(BasePicker.INFINITE_SCROLL_PAGE_COUNT, cellTextAppearance, cellPadding)
```

`INFINITE_SCROLL_PAGE_COUNT` is literally just *1000*. But that number gets multiplied by how many items
your adapter's data set has, plus 1 for a blank padding item. So, the month adapter ends up having
12*1000=12,000 items in the `RecyclerView`. The user is never going to scroll through that many items
so it feels like you could scroll forever. The `BaseColumnAdapter` handles this all for you, so nothing
special is needed other than passing a number larger than 1.

### Listening for Selection Changes

In your picker, the `addPickerColumn` method is used to add your adapter-containing columns. There's
an optional argument to that method called `selectionChange`, which takes an inline-function/lamda:

```kotlin
override fun onShouldAddColumns() {
    addPickerColumn(
        MonthColumnAdapter(
            cellTextAppearance = cellTextAppearance,
            cellPadding = cellPadding
        ),
        selectionChange = {
          // Do something, the selected row/cell in this column has changed.
        }
    )
    // ...
}
```

### Updating Data Sets, and Alias Positions

If you use the `DatePicker`, you'll know that the day of the month is dynamic. January has 31 days,
February has 28, etc. When you switch the selected month, the day of the month picker adjusts automatically.

Internally, the selection change listener shown in the section above is used on the month column.
When a change occurs, we get how many days are in that month and notify the underlying `NumberColumnAdapter`
of the day column of that value:

```kotlin
private fun invalidateDaysOfMonth() {
  val daysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH)
  this.daysOfMonthAdapter.updateMax(daysInMonth)
}
```

---

The `updateMax(Int)` method updates the data set in the adapter and notifies the picker `RecyclerView` of
these changes:

```kotlin
fun updateMax(newMax: Int) {
  if (data.size < newMax) {
    // We need to push new items to the end
    for (i in data.size + 1..newMax) {
      data.add(i)
      aliasPositions(data.size - 1, { notifyItemInserted(it) })
    }
  } else if (data.size > newMax) {
    // We need to pop items off the end
    for (i in data.size - 1 downTo newMax) {
      data.removeAt(i)
      aliasPositions(i, { notifyItemRemoved(it) })
    }
  }
}
```

The `aliasPositions(...)` method used above is pretty important, but only when your adapter is using
a page count larger than 1. This method gets all positions in the adapter that are aliases of a given
position. What does that mean?

```
Data set:    1 2 3 4 5
Data size:   5
Page count:  1000
Total items: 5000
```

In this set of 5000 visual items, the same 5 numbers get repeated 1000 times. The user could select
the value contained at index 1 at 1000 other indices. The `aliasPositions(...)` method emits each of
those 1000 indices into the given inline-function/lambda.

```kotlin
// Indices:      1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21
// Looped data:  1, 2, 3, 4, 5, 1, 2, 3, 4,  5,  1,  2,  3,  4,  5,  1,  2,  3,  4,  5,  6
aliasPositions(1, { print("$it ") })
// The above would print 2, 7, 12, 17, etc. Notice these indices all contain the number 2.
```