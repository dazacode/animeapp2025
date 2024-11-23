package com.kawaidev.kawaime.ui.models.details.episodes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaidev.kawaime.network.dao.api_utils.ApiException
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.helpers.PlayerParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import kotlinx.coroutines.launch

class EpisodesViewModel(private val service: StreamingService) : ViewModel() {

    val episodes = MutableLiveData<Episodes>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchEpisodes(id: String, onError: (ApiException) -> Unit, onLoading: (Boolean) -> Unit, onResult: (Episodes) -> Unit) {
        onLoading(true)
        viewModelScope.launch {
            try {
                val fetchedEpisodes = service.getEpisodes(id)
                onResult(fetchedEpisodes)
            } catch (e: ApiException) {
                onError(e)
            } catch (e: Exception) {
                onError(ApiException(-1, e.message ?: ""))
            } finally {
                onLoading(false)
            }
        }
    }

    fun fetchServers(id: String, onResult: (EpisodeServers) -> Unit, onError: (ApiException) -> Unit, onLoading: (Boolean) -> Unit) {
        onLoading(true)

        viewModelScope.launch {
            try {
                val servers = service.getServers(id)
                onResult(servers)
            } catch (e: ApiException) {
                onError(e)
            } catch (e: Exception) {
                onError(ApiException(-1, e.message ?: ""))
            } finally {
                onLoading(false)
            }
        }
    }

    fun fetchStreaming(
        playerParams: PlayerParams,
        onResult: (Streaming) -> Unit,
        onError: (ApiException) -> Unit,
        onLoading: (Boolean) -> Unit
    ) {
        onLoading(true)

        viewModelScope.launch {
            try {
                val streaming = service.getStreaming(StreamingParams(playerParams.animeEpisodeId, server = playerParams.server, category = playerParams.category))
                onResult(streaming)
            } catch (e: Exception) {
                onError(ApiException(-1, e.message ?: ""))
            } catch (e: ApiException) {
                onError(e)
            } finally {
                onLoading(false)
            }
        }
    }
}
