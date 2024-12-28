package com.kawaidev.kawaime.network.impl

import com.kawaidev.kawaime.network.Request
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.network.routes.StreamingRoutes

class StreamingServiceImpl : StreamingService {

    private val routes = StreamingRoutes

    override suspend fun getEpisodes(id: String): Episodes {
        val url = "${routes.EPISODES}$id/episodes"
        return Request.getRequest(url, "data")
    }

    override suspend fun getServers(id: String): EpisodeServers {
        val url = "${routes.SERVERS}$id"
        return Request.getRequest(url, "data")
    }

    override suspend fun getStreaming(params: StreamingParams): Streaming {
        val url = "${routes.STREAMING}?${params.toQueryString()}"
        return Request.getRequest(url, "data")
    }
}