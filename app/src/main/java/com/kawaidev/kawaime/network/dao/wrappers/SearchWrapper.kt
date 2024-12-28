package com.kawaidev.kawaime.network.dao.wrappers

import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import kotlinx.serialization.Serializable

@Serializable
data class SearchWrapper(
    val animes: List<BasicRelease>,
    val hasNextPage: Boolean
)
