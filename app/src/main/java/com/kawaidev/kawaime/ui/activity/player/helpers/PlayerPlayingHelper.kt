package com.kawaidev.kawaime.ui.activity.player.helpers

import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.helpers.PlayerParams
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import kotlinx.coroutines.launch

object PlayerPlayingHelper {
    fun playNextEpisode(activity: PlayerActivity) {
        val currentEpisodeIndex = activity.episodes.episodes?.indexOfFirst { it.episodeId == activity.id } ?: -1

        activity.save()
        activity.playerViewModel.player?.clearMediaItems()

        activity.lifecycleScope.launch {
            if (currentEpisodeIndex != -1 && currentEpisodeIndex < (activity.episodes.episodes?.size ?: 0) - 1) {
                val nextEpisode = activity.episodes.episodes?.get(currentEpisodeIndex + 1)
                nextEpisode?.let {
                    activity.id = it.episodeId ?: ""
                    activity.episode = it.number?.toString() ?: ""

                    val nextStreaming = activity.service.getStreaming(StreamingParams(
                        it.episodeId ?: "",
                        server = activity.server,
                        category = activity.category)
                    )

                    activity.streaming = nextStreaming

                    PlayerHelper.initializePlayer(activity)
                    PlayerHelper.initializePlayerUI(activity)
                }
            } else {
                Toast.makeText(activity, "No next episode available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun playPreviousEpisode(activity: PlayerActivity) {
        val currentEpisodeIndex = activity.episodes.episodes?.indexOfFirst { it.episodeId == activity.id } ?: -1

        activity.save()
        activity.playerViewModel.player?.clearMediaItems()

        activity.lifecycleScope.launch {
            if (currentEpisodeIndex > 0) {
                val prevEpisode = activity.episodes.episodes?.get(currentEpisodeIndex - 1)
                prevEpisode?.let {
                    activity.id = it.episodeId ?: ""
                    activity.episode = it.number?.toString() ?: ""

                    val nextStreaming = activity.service.getStreaming(StreamingParams(
                        it.episodeId ?: "",
                        server = activity.server,
                        category = activity.category)
                    )

                    activity.streaming = nextStreaming

                    PlayerHelper.initializePlayer(activity)
                    PlayerHelper.initializePlayerUI(activity)
                }
            } else {
                Toast.makeText(activity, "No previous episode available", Toast.LENGTH_SHORT).show()
            }
        }
    }

}