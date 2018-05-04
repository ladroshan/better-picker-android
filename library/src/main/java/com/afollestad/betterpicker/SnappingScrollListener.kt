package com.afollestad.betterpicker

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import java.lang.Math.abs

internal class SnappingScrollListener(
  private val layoutManager: LinearLayoutManager,
  private val selectionChange: (() -> (Unit))? = null
) : RecyclerView.OnScrollListener() {
  private var lastMiddleVisible = -1

  override fun onScrollStateChanged(
    recyclerView: RecyclerView?,
    newState: Int
  ) {
    super.onScrollStateChanged(recyclerView, newState)
    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
      val center = centerForView(recyclerView!!)
      val currentView = findClosestView(center)
      val closestViewCenter = centerForView(currentView!!)
      val scrollAmount = closestViewCenter - center
      recyclerView.smoothScrollBy(0, scrollAmount)
      val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
      if (firstVisible + 1 != lastMiddleVisible) {
        selectionChange?.invoke()
        lastMiddleVisible = firstVisible + 1
      }
    }
  }

  private fun findClosestView(center: Int): View? {
    var minDistance = Integer.MAX_VALUE
    var result: View? = null
    for (i in layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()) {
      val view = layoutManager.findViewByPosition(i)
      val viewCenter = centerForView(view)
      val distance = abs(viewCenter - center)
      if (distance <= minDistance) {
        minDistance = distance
        result = view
      }
    }
    return result
  }

  private fun centerForView(view: View): Int {
    return (view.top + view.bottom) / 2
  }
}
