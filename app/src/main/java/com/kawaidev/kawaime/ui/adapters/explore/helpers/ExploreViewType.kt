package com.kawaidev.kawaime.ui.adapters.explore.helpers

object ExploreViewType {
    const val VIEW_TYPE_ACTION = 0
    const val VIEW_TYPE_DIVIDER = 1
    const val VIEW_TYPE_LATEST_NEWS = 2
    const val VIEW_TYPE_LOADING = 3
    const val VIEW_TYPE_ERROR = 4

    fun getItemViewType(isLoading: Boolean, isError: Boolean, position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_ACTION
            1 -> VIEW_TYPE_DIVIDER
            2 -> {
                if (isLoading) {
                    VIEW_TYPE_LOADING
                } else if (isError) {
                    VIEW_TYPE_ERROR
                } else {
                    VIEW_TYPE_LATEST_NEWS
                }
            }
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    fun getItemCount(): Int {
        // Always return 3 items: ACTION, DIVIDER, and either LATEST_NEWS, LOADING, or ERROR
        return 3
    }
}