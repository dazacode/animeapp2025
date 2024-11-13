package com.kawaidev.kawaime.network.dao.streaming

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeServers(
    val episodeId: String? = null,
    val episodeNo: Int? = null,
    val sub: List<Server> = emptyList(),
    val dub: List<Server> = emptyList(),
    val raw: List<Server> = emptyList()
)

@Serializable
data class Server(
    val serverId: Int? = null,
    val serverName: String? = null
)