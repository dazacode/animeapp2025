package com.kawaidev.kawaime.network.dao.anime

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val latestEpisodeAnimes: List<BasicRelease> = emptyList(),
    var spotlightAnimes: List<BasicRelease> = emptyList(),
    val topAiringAnimes: List<BasicRelease> = emptyList(),
    val topUpcomingAnimes: List<BasicRelease> = emptyList(),
    val trendingAnimes: List<BasicRelease> = emptyList(),
    val mostPopularAnimes: List<BasicRelease> = emptyList(),
    val mostFavoriteAnimes: List<BasicRelease> = emptyList(),
    val latestCompletedAnimes: List<BasicRelease> = emptyList(),
    val top10Animes: Top10Animes = Top10Animes()
)

@Serializable
data class Top10Animes(
    val today: List<BasicRelease> = emptyList(),
    val month: List<BasicRelease> = emptyList(),
    val week: List<BasicRelease> = emptyList()
)