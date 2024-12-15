package com.kawaidev.kawaime.ui.adapters.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearDecoration(
    private val vertical: Int = 0,
    private val horizontal: Int = 0,
    private val edges: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val itemCount = state.itemCount

        outRect.set(0, 0, 0, 0)

        outRect.top = vertical
        outRect.bottom = vertical

        outRect.left = if (position == 0) edges else horizontal / 2
        outRect.right = if (position == itemCount - 1) edges else horizontal / 2
    }
}