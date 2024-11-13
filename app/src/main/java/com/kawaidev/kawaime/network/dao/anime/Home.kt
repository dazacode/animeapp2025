package com.kawaidev.kawaime.network.dao.anime

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val latestEpisodeAnimes: List<SearchResponse> = emptyList(),
    var spotlightAnimes: List<SearchResponse> = emptyList(),
    val topAiringAnimes: List<SearchResponse> = emptyList(),
    val topUpcomingAnimes: List<SearchResponse> = emptyList(),
    val trendingAnimes: List<SearchResponse> = emptyList(),
    val mostPopularAnimes: List<SearchResponse> = emptyList(),
    val mostFavoriteAnimes: List<SearchResponse> = emptyList(),
    val latestCompletedAnimes: List<SearchResponse> = emptyList(),
    val top10Animes: Top10Animes = Top10Animes()
)

@Serializable
data class Top10Animes(
    val today: List<SearchResponse> = emptyList(),
    val month: List<SearchResponse> = emptyList(),
    val week: List<SearchResponse> = emptyList()
)