package com.kawaidev.kawaime.ui.adapters.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

class DividerDecoration(
    context: Context,
    private val size: Int, // Size of the divider in pixels
    @ColorInt private val color: Int, // Divider color
    private val includeLast: Boolean // Whether to include the last item
) : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint()

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val childCount = parent.childCount
        val itemCount = parent.adapter?.itemCount ?: 0

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            // Skip last item if includeLast is false
            if (!includeLast && position == itemCount - 1) {
                continue
            }

            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val top = child.bottom + params.bottomMargin
            val bottom = top + size

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        // Add bottom offset for each item, skip last if includeLast is false
        if (includeLast || position < itemCount - 1) {
            outRect.set(0, 0, 0, size)
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }
}