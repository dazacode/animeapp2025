package com.kawaidev.kawaime.ui.adapters.helpers

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeViewType
import com.kawaidev.kawaime.utils.Converts

object GridRecycler {
    fun setup(context: Context, adapter: AnimeAdapter, recycler: RecyclerView) {
        val spanCount = calculateSpanCount(context)
        val space = Converts.dpToPx(6f, context).toInt()

        val gridLayoutManager = GridLayoutManager(context, spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    AnimeViewType.VIEW_TYPE_LOADING -> spanCount
                    AnimeViewType.VIEW_TYPE_BOTTOM_LOADING -> spanCount
                    AnimeViewType.VIEW_TYPE_ERROR -> spanCount
                    AnimeViewType.VIEW_TYPE_EMPTY -> spanCount
                    else -> 1
                }
            }
        }

        recycler.layoutManager = gridLayoutManager

        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(
                SpacingItemDecoration(
                    Spacing(
                        horizontal = space,
                        vertical = space,
                        edges = Rect(space, space, space, space)
                    )
                )
            )
        }
    }

    private fun calculateSpanCount(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val itemWidth = context.resources.getDimensionPixelSize(R.dimen.anime_item_width)
        return maxOf(1, screenWidth / itemWidth)
    }
}