package com.kawaidev.kawaime.ui.adapters.home.helpers

import com.kawaidev.kawaime.network.dao.anime.Home

object ViewTypes {
    const val VIEW_TYPE_LOADING = 0
    const val VIEW_TYPE_ERROR = 1
    const val VIEW_TYPE_SPOTLIGHT = 2
    const val VIEW_TYPE_LATEST = 3
    const val VIEW_TYPE_TRENDING = 4
    const val VIEW_TYPE_TOP_AIRING = 5
    const val VIEW_TYPE_POPULAR = 6
    const val VIEW_TYPE_MOST_FAVORITE = 7
    const val VIEW_TYPE_LATEST_COMPLETED = 8
    const val VIEW_TYPE_TOP_10_TODAY = 9
    const val VIEW_TYPE_TOP_10_WEEK = 10
    const val VIEW_TYPE_TOP_10_MONTH = 11

    fun getViewType(position: Int, isLoading: Boolean, isError: Boolean, home: Home): Int {
        if (isLoading) return VIEW_TYPE_LOADING
        if (isError) return VIEW_TYPE_ERROR

        val categories = listOf(
            home.spotlightAnimes to VIEW_TYPE_SPOTLIGHT,
            home.latestEpisodeAnimes to VIEW_TYPE_LATEST,
            home.trendingAnimes to VIEW_TYPE_TRENDING,
            home.topAiringAnimes to VIEW_TYPE_TOP_AIRING,
            home.mostPopularAnimes to VIEW_TYPE_POPULAR,
            home.mostFavoriteAnimes to VIEW_TYPE_MOST_FAVORITE,
            home.latestCompletedAnimes to VIEW_TYPE_LATEST_COMPLETED,
            home.top10Animes.today to VIEW_TYPE_TOP_10_TODAY,
            home.top10Animes.week to VIEW_TYPE_TOP_10_WEEK,
            home.top10Animes.month to VIEW_TYPE_TOP_10_MONTH
        )

        var currentPos = 0
        for ((list, viewType) in categories) {
            if (list.isNotEmpty() && position == currentPos) {
                return viewType
            }
            currentPos++
        }

        return VIEW_TYPE_ERROR
    }

    fun getItemCount(isLoading: Boolean, isError: Boolean, home: Home): Int {
        return when {
            isLoading || isError -> 1
            else -> {
                var count = 0
                if (home.spotlightAnimes.isNotEmpty()) count++
                if (home.latestEpisodeAnimes.isNotEmpty()) count++
                if (home.trendingAnimes.isNotEmpty()) count++
                if (home.topAiringAnimes.isNotEmpty()) count++
                if (home.mostPopularAnimes.isNotEmpty()) count++
                if (home.mostFavoriteAnimes.isNotEmpty()) count++
                if (home.latestCompletedAnimes.isNotEmpty()) count++
                if (home.top10Animes.today.isNotEmpty()) count++
                if (home.top10Animes.week.isNotEmpty()) count++
                if (home.top10Animes.month.isNotEmpty()) count++
                count
            }
        }
    }
}