package com.kawaidev.kawaime.ui.models.details.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeDetail
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Server
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import kotlinx.coroutines.launch

class EpisodesViewModel(private val service: StreamingService) : ViewModel() {

    val episodes = MutableLiveData<Episodes>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchEpisodes(id: String, onError: (String) -> Unit, onLoading: (Boolean) -> Unit, onResult: (Episodes) -> Unit) {
        onLoading(true)
        viewModelScope.launch {
            try {
                val fetchedEpisodes = service.getEpisodes(id)
                onResult(fetchedEpisodes)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            } finally {
                onLoading(false)
            }
        }
    }

    fun fetchServers(id: String, onResult: (EpisodeServers) -> Unit, onError: (String) -> Unit, onLoading: (Boolean) -> Unit) {
        onLoading(true)

        viewModelScope.launch {
            try {
                val servers = service.getServers(id)
                onResult(servers)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            } finally {
                onLoading(false)
            }
        }
    }

    fun fetchStreaming(
        streamingParams: StreamingParams,
        onResult: (Streaming) -> Unit,
        onError: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        onLoading(true)

        viewModelScope.launch {
            try {
                val streaming = service.getStreaming(streamingParams)
                onResult(streaming)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            } finally {
                onLoading(false)
            }
        }
    }
}
