package com.kawaidev.kawaime.network.dao.streaming

data class HlsStreamInfo(
    val url: String,
    val bandwidth: Int,
    val resolution: String
)