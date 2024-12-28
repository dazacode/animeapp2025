package com.kawaidev.kawaime.ui.activity.player.helpers

import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import com.kawaidev.kawaime.ui.activity.player.helpers.utils.PlayerUtils
import kotlinx.coroutines.launch

object PlayerHelper {

    @OptIn(UnstableApi::class)
    fun initializePlayer(activity: PlayerActivity) {
        val trackSelector = DefaultTrackSelector(activity)
        activity.playerViewModel.player = activity.playerViewModel.player ?: ExoPlayer.Builder(activity)
            .setTrackSelector(trackSelector)
            .build()

        val streamingUrl = activity.streaming.sources?.firstOrNull { it.type == "hls" }?.url ?: return

        activity.url = streamingUrl
        activity.lifecycleScope.launch {
            try {
                val mediaItem = PlayerUtils.buildMediaItemWithSubtitles(activity.url, activity.streaming)
                activity.playerViewModel.player?.apply {
                    clearMediaItems()
                    setMediaItem(mediaItem)
                    prepare()
                    handlePlayerState(activity)
                }
            } catch (e: Exception) {
                Log.e("PlayerActivity", "Error initializing player with master HLS URL: ${e.message}")
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun ExoPlayer.handlePlayerState(activity: PlayerActivity) {
        val wasPlaying = activity.playerViewModel.playWhenReady
        setHandleAudioBecomingNoisy(true)

        val watched = activity.prefs.findByEpisodeId(activity.params.animeEpisodeId)
        if (watched != null) seekTo(watched.watchedTo)

        addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                removeListener(this)
                activity.playerViewModel.playWhenReady = playWhenReady || wasPlaying
            }
        })
    }
}