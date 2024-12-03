package com.kawaidev.kawaime.ui.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.kawaidev.kawaime.network.dao.anime.Home

class HomeDiffCallback(
    private val oldHome: Home,
    private val newHome: Home
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return countNonEmptyLists(oldHome)
    }

    override fun getNewListSize(): Int {
        return countNonEmptyLists(newHome)
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare based on the type of list (spotlightAnimes, latestEpisodeAnimes, etc.)
        return getItemAtPosition(oldItemPosition) == getItemAtPosition(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare contents of the list at the given positions
        val oldItem = getItemAtPosition(oldItemPosition)
        val newItem = getItemAtPosition(newItemPosition)
        return oldItem == newItem
    }

    // Helper function to count the number of non-empty lists in the Home object
    private fun countNonEmptyLists(home: Home): Int {
        var count = 0
        if (home.spotlightAnimes.isNotEmpty()) count++
        if (home.latestEpisodeAnimes.isNotEmpty()) count++
        if (home.trendingAnimes.isNotEmpty()) count++
        if (home.topAiringAnimes.isNotEmpty()) count++
        if (home.mostPopularAnimes.isNotEmpty()) count++
        if (home.mostFavoriteAnimes.isNotEmpty()) count++
        if (home.latestCompletedAnimes.isNotEmpty()) count++
        return count
    }

    // Helper function to get the list at a specific position
    private fun getItemAtPosition(position: Int): Any {
        return when (position) {
            0 -> oldHome.spotlightAnimes
            1 -> oldHome.latestEpisodeAnimes
            2 -> oldHome.trendingAnimes
            3 -> oldHome.topAiringAnimes
            4 -> oldHome.mostPopularAnimes
            5 -> oldHome.mostFavoriteAnimes
            6 -> oldHome.latestCompletedAnimes
            else -> throw IndexOutOfBoundsException("Invalid position")
        }
    }
}