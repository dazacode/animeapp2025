package com.kawaidev.kawaime.network.dao.anime

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: String,
    val time: String,
    val name: String,
    val jname: String,
    val airingTimestamp: Long,
    val secondsUntilAiring: Int,
    val episode: Int
)