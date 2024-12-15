package com.kawaidev.kawaime.ui.activity.player.helpers

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Server
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import kotlinx.coroutines.launch

object PlayerPlayingHelper {
    fun playNextEpisode(activity: PlayerActivity) {
        val currentEpisodeIndex = activity.params.episodes.episodes?.indexOfFirst { it.episodeId == activity.params.animeEpisodeId } ?: -1

        activity.save()
        activity.playerViewModel.player?.clearMediaItems()

        handleEpisodePlayback(activity, currentEpisodeIndex + 1) { "No next episode available" }
    }

    fun playPreviousEpisode(activity: PlayerActivity) {
        val currentEpisodeIndex = activity.params.episodes.episodes?.indexOfFirst { it.episodeId == activity.params.animeEpisodeId } ?: -1

        activity.save()
        activity.playerViewModel.player?.clearMediaItems()

        handleEpisodePlayback(activity, currentEpisodeIndex - 1) { "No previous episode available" }
    }

    private fun handleEpisodePlayback(activity: PlayerActivity, episodeIndex: Int, onError: () -> String) {
        activity.lifecycleScope.launch {
            val episodes = activity.params.episodes.episodes
            if (episodes != null && episodeIndex in episodes.indices) {
                val episode = episodes[episodeIndex]
                activity.params.animeEpisodeId = episode.episodeId ?: ""
                activity.params.episode = episode.number?.toString() ?: ""

                val availableServers = activity.service.getServers(episode.episodeId ?: "")
                val selectedServer = findAvailableServer(availableServers)

                if (selectedServer != null) {
                    val streaming = activity.service.getStreaming(
                        StreamingParams(
                            episode.episodeId ?: "",
                            server = selectedServer.serverName ?: "",
                            category = determineCategory(availableServers, selectedServer)
                        )
                    )

                    activity.streaming = streaming

                    PlayerHelper.initializePlayer(activity)
                    PlayerHelper.initializePlayerUI(activity)
                } else {
                    Toast.makeText(activity, "No available server found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, onError(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findAvailableServer(servers: EpisodeServers): Server? {
        // Priority: Sub > Dub > Raw
        return servers.sub.firstOrNull()
            ?: servers.dub.firstOrNull()
            ?: servers.raw.firstOrNull()
    }

    private fun determineCategory(servers: EpisodeServers, server: Server): String {
        return when {
            servers.sub.contains(server) -> "sub"
            servers.dub.contains(server) -> "dub"
            servers.raw.contains(server) -> "raw"
            else -> "sub"
        }
    }
}