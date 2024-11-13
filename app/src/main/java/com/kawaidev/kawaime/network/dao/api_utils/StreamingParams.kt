package com.kawaidev.kawaime.network.dao.api_utils

data class StreamingParams(
    private val animeEpisodeId: String,
    private val server: String = "hd-1",
    private val category: String = "sub"
) {
    fun toQueryString(): String {
        return listOf(
            "animeEpisodeId=${animeEpisodeId}",
            "server=${server}",
            "category=${category}"
        ).joinToString("&")
    }
}