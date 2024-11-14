package com.kawaidev.kawaime.network.dao.helpers

import kotlinx.serialization.Serializable

@Serializable
data class WatchedRelease(
    val episodeId: String,
    val watchedTo: Long
)