package com.kawaidev.kawaime.network.dao.helpers

import com.kawaidev.kawaime.network.dao.streaming.Episodes
import kotlinx.serialization.Serializable

@Serializable
data class PlayerParams(
    var animeEpisodeId: String = "",
    var episode: String = "",
    val title: String = "",
    val episodes: Episodes = Episodes(),
    val server: String = "hd-1",
    val category: String = "sub",
    val download: Boolean = false,
)