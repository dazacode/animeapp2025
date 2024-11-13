package com.kawaidev.kawaime.ui.custom

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearSpacingItemDecoration(
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    private val spacingPx: Int by lazy {
        (spacing * Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (includeEdge) {
            outRect.left = if (position == 0) spacingPx else spacingPx / 2
            outRect.right = if (position == itemCount - 1) spacingPx else spacingPx / 2
        } else {
            outRect.left = spacingPx / 2
            outRect.right = spacingPx / 2
        }
    }
}
