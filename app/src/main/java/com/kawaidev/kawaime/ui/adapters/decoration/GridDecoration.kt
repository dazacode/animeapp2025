package com.kawaidev.kawaime.ui.adapters.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridDecoration(
    private val horizontal: Int = 0,
    private val vertical: Int = 0,
    private val edges: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = vertical / 2
        outRect.top = horizontal / 2
        outRect.right = vertical / 2
        outRect.bottom = horizontal / 2

        parent.setPadding(edges / 2, edges / 2, edges / 2, edges / 2)
        parent.clipToPadding = false
    }
}