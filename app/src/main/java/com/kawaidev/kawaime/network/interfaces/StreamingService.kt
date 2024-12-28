package com.kawaidev.kawaime.network.interfaces

import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.impl.StreamingServiceImpl

interface StreamingService {
    suspend fun getEpisodes(id: String): Episodes
    suspend fun getServers(id: String): EpisodeServers
    suspend fun getStreaming(params: StreamingParams): Streaming

    companion object {
        fun create(): StreamingService {
            return StreamingServiceImpl()
        }
    }
}