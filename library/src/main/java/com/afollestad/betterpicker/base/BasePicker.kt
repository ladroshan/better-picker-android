package com.afollestad.betterpicker.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.afollestad.betterpicker.R
import com.afollestad.betterpicker.SnappingScrollListener
import com.afollestad.betterpicker.getSpaceForText
import com.afollestad.betterpicker.inflate
import com.afollestad.betterpicker.setTextAppearanceCompat

/** Provides a common base for pickers, e.g. date and time pickers. */
abstract class BasePicker(
  context: Context,
  attrs: AttributeSet
) : FrameLayout(context, attrs) {

  companion object {
    const val INFINITE_SCROLL_PAGE_COUNT = 1000
  }

  private var cellSize: Int
  private var dividerPaint: Paint
  private var overlayBackground: Int
  private var overlayOpacity: Float
  private var didInit = false

  private var overlayOne: View? = null
  private var overlayTwo: View? = null
  private var pickers: List<RecyclerView> = mutableListOf()

  val cellTextAppearance: Int
  val cellPadding: Int

  init {
    val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker)
    val dividerColor: Int
    val dividerHeight: Float
    try {
      overlayBackground =
          attributesArray.getColor(
              R.styleable.BasePicker_pickerOverlayBackground, Color.BLACK
          )
      overlayOpacity =
          attributesArray.getFloat(
              R.styleable.BasePicker_pickerOverlayOpacity, 0.8f
          )
      dividerColor =
          attributesArray.getColor(
              R.styleable.BasePicker_pickerOverlayOpacity,
              ContextCompat.getColor(
                  context,
                  R.color.default_gray
              )
          )
      dividerHeight = attributesArray.getDimension(
          R.styleable.BasePicker_pickerDividerHeight,
          resources.getDimension(
              R.dimen.default_divider_height
          )
      )
      cellTextAppearance =
          attributesArray.getResourceId(
              R.styleable.BasePicker_pickerCellTextAppearance, R.style.DefaultItemTextAppearance
          )
      cellPadding = attributesArray.getDimensionPixelSize(
          R.styleable.BasePicker_pickerCellPadding,
          resources.getDimensionPixelSize(R.dimen.default_picker_item_padding)
      )
    } finally {
      attributesArray.recycle()
    }

    setWillNotDraw(false)
    dividerPaint = Paint()
    dividerPaint.color = dividerColor
    dividerPaint.style = STROKE
    dividerPaint.strokeWidth = dividerHeight
    cellSize = calculateCellSize()
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    // We want to show 3 rows on screen per column (variable columns)
    setMeasuredDimension(width, cellSize * 3)
    if (measuredWidth == 0 || didInit) {
      return
    }
    didInit = true
    onShouldAddColumns()
    addOverlays()
    notifyReady()
  }

  private fun notifyReady() {
    for (picker in pickers) {
      val adapter = picker.adapter as BaseColumnAdapter<*>
      adapter.onReady()
    }
    onReadyForInitialization()
  }

  /**
   * Called when inheriting classes should add their columns with [addPickerColumn]. At this point
   * the base picker will have been measured so columns can be successfully added.
   */
  abstract fun onShouldAddColumns()

  /**
   * Called after [onShouldAddColumns], and after overlays have been added.
   * Any other initialization should be done.
   */
  abstract fun onReadyForInitialization()

  /** Draws divider lines at the 1/3 and 2/3 height points. */
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val oneThird = measuredHeight * (1 / 3f)
    val twoThirds = measuredHeight * (2 / 3f)
    canvas.drawLine(0f, oneThird, measuredWidth.toFloat(), oneThird, dividerPaint)
    canvas.drawLine(0f, twoThirds, measuredWidth.toFloat(), twoThirds, dividerPaint)
  }

  fun addPickerColumn(
    adapter: BaseColumnAdapter<*>,
    selectionChange: (() -> (Unit))? = null
  ) {
    if (!didInit) {
      throw IllegalStateException(
          "Cannot use addPickerColumn until onReadyForInitialization() is invoked."
      )
    }
    val picker = createRecyclerView(selectionChange)
    picker.adapter = adapter
    pickers += picker
    addView(picker)
  }

  /** Scrolls to a value in a specified column. */
  @Suppress("UNCHECKED_CAST")
  fun <IT> selectValueInColumn(
    columnIndex: Int,
    value: IT
  ) {
    val picker = pickers[columnIndex]
    val adapter = picker.adapter as BaseColumnAdapter<IT>
    val position = adapter.positionOf(value)
    picker.scrollToPosition(position - 1)
  }

  /** Gets the selected value for a specified column. */
  @Suppress("UNCHECKED_CAST")
  fun <IT> getSelectedValueInColumn(columnIndex: Int): IT {
    val picker = pickers[columnIndex]
    val adapter = picker.adapter as BaseColumnAdapter<IT>
    val layoutManager = picker.layoutManager as LinearLayoutManager
    val position = layoutManager.findFirstCompletelyVisibleItemPosition() + 1
    return adapter.valueForPosition(position)
  }

  /** Adds the semi transparent overlay views that go over the top third and bottom third of the parent. */
  private fun addOverlays() {
    if (measuredWidth == 0 || (overlayOne != null && overlayTwo != null)) {
      return
    }
    overlayOne = View(context)
    overlayOne!!.background = ColorDrawable(overlayBackground)
    overlayOne!!.alpha = overlayOpacity
    addView(overlayOne)
    overlayTwo = View(context)
    overlayTwo!!.background = ColorDrawable(overlayBackground)
    overlayTwo!!.alpha = overlayOpacity
    addView(overlayTwo)
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int
  ) {
    // Lays out each picker evenly in the parent's available width.
    val xIncrementBy = (measuredWidth * (1f / pickers.size)).toInt()
    var startX = 0
    for (picker in pickers) {
      val endX = startX + xIncrementBy
      picker.layout(startX, 0, endX, measuredHeight)
      startX = endX
    }

    // Places the overlays at the top and bottom thirds of the parent height.
    val oneThirdHeight = (measuredHeight * (1 / 3f)).toInt()
    val twoThirdsHeight = (measuredHeight * (2 / 3f)).toInt()
    overlayOne!!.layout(0, 0, measuredWidth, oneThirdHeight - 1)
    overlayTwo!!.layout(0, twoThirdsHeight + 1, measuredWidth, measuredHeight)
  }

  /** Creates a RecyclerView which is added as a column to the parent. */
  private fun createRecyclerView(selectionChange: (() -> (Unit))?): RecyclerView {
    val recyclerView = RecyclerView(context)
    recyclerView.layoutParams =
        FrameLayout.LayoutParams(measuredWidth / 3, cellSize * 3)
    val layoutManager = LinearLayoutManager(context)
    recyclerView.layoutManager = layoutManager
    recyclerView.addOnScrollListener(
        SnappingScrollListener(layoutManager, selectionChange)
    )
    recyclerView.setHasFixedSize(true)
    return recyclerView
  }

  /** This must only be called once. Calculates the width and height of an average cell. */
  private fun calculateCellSize(): Int {
    val view: TextView = inflate(R.layout.picker_item)
    view.setTextAppearanceCompat(cellTextAppearance)
    return view.getSpaceForText("PM", cellPadding * 2)
  }
}
