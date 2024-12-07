package com.kawaidev.kawaime.ui.adapters.details.helpers

import com.kawaidev.kawaime.network.dao.anime.Release

object DetailsViewType {
    const val VIEW_TYPE_LOADING = 0
    const val VIEW_TYPE_ERROR = 1
    const val VIEW_TYPE_HEADER = 2
    const val VIEW_TYPE_DESCRIPTION = 3
    const val VIEW_TYPE_SEASONS = 4
    const val VIEW_TYPE_RELATED = 5
    const val VIEW_TYPE_RECOMMENDATIONS = 6
    const val VIEW_TYPE_SCREENSHOTS = 7

    fun getItemViewType(isLoading: Boolean, isError: Boolean, position: Int, release: Release): Int {
        return if (isLoading && !isError) {
            VIEW_TYPE_LOADING
        } else if (isError && !isLoading) {
            VIEW_TYPE_ERROR
        } else {
            var currentPos = 0
            if (currentPos == position) return VIEW_TYPE_HEADER
            currentPos++

            if (release.anime?.info?.description.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_DESCRIPTION
                currentPos++
            }
            if (release.shikimori?.screenshots.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_SCREENSHOTS
                currentPos++
            }
            if (release.seasons.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_SEASONS
                currentPos++
            }
            if (release.relatedAnimes.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_RELATED
                currentPos++
            }
            if (release.recommendedAnimes.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_RECOMMENDATIONS
            }
            throw IllegalArgumentException("Invalid view type at position $position")
        }
    }

    fun getItemCount(isLoading: Boolean, isError: Boolean, release: Release): Int {
        if (isLoading && !isError) return 1
        if (isError && !isLoading) return 1

        var count = 0
        if (release.anime != null) count++
        if (release.anime?.info?.description.isNullOrEmpty().not()) count++
        if (release.seasons.isNullOrEmpty().not()) count++
        if (release.shikimori?.screenshots.isNullOrEmpty().not()) count++ // Increment if screenshots exist
        if (release.relatedAnimes.isNullOrEmpty().not()) count++
        if (release.recommendedAnimes.isNullOrEmpty().not()) count++

        return count
    }

    fun getItemTypeAtPosition(position: Int, release: Release): Int {
        return getItemViewType(false, false, position, release)
    }
}