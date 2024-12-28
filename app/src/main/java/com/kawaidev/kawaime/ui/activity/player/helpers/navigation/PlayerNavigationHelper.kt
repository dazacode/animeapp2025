package com.kawaidev.kawaime.ui.activity.player.helpers.navigation

import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import com.kawaidev.kawaime.ui.activity.player.helpers.playing.PlayerPlayingHelper

object PlayerNavigationHelper {

    fun seekForward(activity: PlayerActivity) {
        val currentPosition = activity.playerViewModel.player?.currentPosition ?: 0L
        activity.playerViewModel.player?.seekTo(currentPosition + 85000)
    }

    fun playNextEpisode(activity: PlayerActivity) {
        PlayerPlayingHelper.playNextEpisode(activity)
    }

    fun playPreviousEpisode(activity: PlayerActivity) {
        PlayerPlayingHelper.playPreviousEpisode(activity)
    }

    fun hasNextEpisode(activity: PlayerActivity): Boolean {
        val currentIndex = getCurrentEpisodeIndex(activity)
        return currentIndex < (activity.params.episodes.episodes?.size ?: 0) - 1
    }

    fun hasPreviousEpisode(activity: PlayerActivity): Boolean {
        val currentIndex = getCurrentEpisodeIndex(activity)
        return currentIndex > 0
    }

    private fun getCurrentEpisodeIndex(activity: PlayerActivity): Int {
        return activity.params.episodes.episodes?.indexOfFirst { it.episodeId == activity.params.animeEpisodeId } ?: -1
    }
}