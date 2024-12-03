package com.kawaidev.kawaime.network.dao.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BasicRelease(
    val rank: Int? = null,
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val poster: String? = null,
    val jname: String? = null,
    val episodes: EpisodeCounts? = null,
    val type: String? = null,
    val rating: String? = null,
    @SerialName("otherInfo") val otherInfo: List<String>? = null
)