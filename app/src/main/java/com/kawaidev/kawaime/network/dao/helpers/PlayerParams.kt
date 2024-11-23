package com.kawaidev.kawaime.network.dao.helpers

import com.kawaidev.kawaime.network.dao.streaming.Episodes

data class PlayerParams(
    val animeEpisodeId: String,
    val episode: String,
    val title: String,
    val episodes: Episodes,
    val server: String = "hd-1",
    val category: String = "sub",
    val download: Boolean = false,
) {
    fun toQueryString(): String {
        return listOf(
            "animeEpisodeId=${animeEpisodeId}",
            "server=${server}",
            "category=${category}"
        ).joinToString("&")
    }
}