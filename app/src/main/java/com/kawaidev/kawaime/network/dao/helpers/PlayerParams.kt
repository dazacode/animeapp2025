package com.kawaidev.kawaime.network.dao.helpers

import com.kawaidev.kawaime.network.dao.streaming.Episodes

data class PlayerParams(
    var animeEpisodeId: String = "",
    var episode: String = "",
    val title: String = "",
    val episodes: Episodes = Episodes(),
    val server: String = "hd-1",
    val category: String = "sub",
    val download: Boolean = false,
)