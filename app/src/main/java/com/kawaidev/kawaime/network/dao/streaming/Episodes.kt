package com.kawaidev.kawaime.network.dao.streaming

import kotlinx.serialization.Serializable

@Serializable
data class Episodes(
    val totalEpisodes: Int? = null,
    var episodes: List<EpisodeDetail>? = null
)

@Serializable
data class EpisodeDetail(
    val title: String? = null,
    val episodeId: String? = null,
    val number: Int? = null,
    val isFiller: Boolean? = null
)