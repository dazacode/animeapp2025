package com.kawaidev.kawaime.network.dao.api_utils

data class StreamingParams(
    val animeEpisodeId: String,
    val server: String = "hd-1",
    val category: String = "sub"
) {
    fun toQueryString(): String {
        return listOf(
            "animeEpisodeId=${animeEpisodeId}",
            "server=${server}",
            "category=${category}"
        ).joinToString("&")
    }
}