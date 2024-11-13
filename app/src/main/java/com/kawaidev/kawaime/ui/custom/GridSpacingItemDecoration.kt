package com.kawaidev.kawaime.ui.custom

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean,
    private val excludeItems: List<Int> = emptyList(),
    private val isHeaderEnabled: Boolean = false
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
        val position = parent.getChildAdapterPosition(view) // item position

        // If the item position is in the exclude list, set no offset
        if (position in excludeItems) {
            outRect.set(0, 0, 0, 0)
            return
        }

        if (isHeaderEnabled && position > 0) {
            // Adjust position to account for the header taking full span
            val adjustedPosition = position - 1
            val column = adjustedPosition % spanCount // item column (after header)

            if (includeEdge) {
                // Left and right spacing
                outRect.left = spacingPx - column * spacingPx / spanCount
                outRect.right = (column + 1) * spacingPx / spanCount

                // Top spacing for first row (after header)
                if (adjustedPosition < spanCount) { // first row of items after header
                    outRect.top = spacingPx
                }

                // Bottom spacing for all items
                outRect.bottom = spacingPx
            } else {
                // Left and right spacing for non-edge case
                outRect.left = column * spacingPx / spanCount
                outRect.right = spacingPx - (column + 1) * spacingPx / spanCount

                // Top spacing for all rows except the first one
                if (adjustedPosition >= spanCount) {
                    outRect.top = spacingPx
                }
            }
        } else {
            // Default behavior: Apply spacing without header adjustment
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = spacingPx - column * spacingPx / spanCount
                outRect.right = (column + 1) * spacingPx / spanCount
                if (position < spanCount) { // top edge
                    outRect.top = spacingPx
                }
                outRect.bottom = spacingPx // item bottom
            } else {
                outRect.left = column * spacingPx / spanCount
                outRect.right = spacingPx - (column + 1) * spacingPx / spanCount
                if (position >= spanCount) {
                    outRect.top = spacingPx // item top
                }
            }
        }
    }
}
