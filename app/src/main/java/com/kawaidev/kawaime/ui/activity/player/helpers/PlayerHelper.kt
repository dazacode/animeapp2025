package com.kawaidev.kawaime.ui.activity.player.helpers

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import androidx.annotation.OptIn
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import kotlinx.coroutines.launch
import kotlin.math.max

object PlayerHelper {

    @OptIn(UnstableApi::class)
    fun initializePlayer(activity: PlayerActivity) {
        val trackSelector = DefaultTrackSelector(activity)

        activity.playerViewModel.player = ExoPlayer.Builder(activity)
            .setTrackSelector(trackSelector)
            .build()

        val streamingUrl = activity.streaming.sources?.firstOrNull { it.type == "hls" }?.url

        if (streamingUrl != null) {
            activity.url = streamingUrl

            activity.lifecycleScope.launch {
                try {
                    val mediaItem = buildMediaItemWithSubtitles(activity.url, activity.streaming)
                    activity.playerViewModel.player?.setMediaItem(mediaItem)
                    activity.playerViewModel.player?.prepare()
                    activity.playerViewModel.player?.setHandleAudioBecomingNoisy(true)

                    activity.playerViewModel.player?.addListener(object : Player.Listener {
                        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                            if (playbackState == Player.STATE_READY) {
                                val watched = activity.prefs.findByEpisodeId(activity.id)
                                if (watched != null) {
                                    activity.playerViewModel.player?.seekTo(watched.watchedTo)
                                }
                                activity.playerViewModel.player?.removeListener(this)
                                activity.playerViewModel.player?.playWhenReady = playWhenReady
                            }
                        }
                    })
                } catch (e: Exception) {
                    Log.e("PlayerActivity", "Error initializing player with master HLS URL: ${e.message}")
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun initializePlayerUI(activity: PlayerActivity) {
        activity.playerView.player = activity.playerViewModel.player

        activity.playerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                activity.playerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(activity.playerView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val horizontalMargin = max(insets.left, insets.right)

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = horizontalMargin
                rightMargin = horizontalMargin
                bottomMargin = insets.bottom
            }

            WindowInsetsCompat.CONSUMED
        }


        activity.playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) showSystemUI(activity) else hideSystemUI(activity)
        })

        activity.playerView.findViewById<ImageButton>(R.id.button_back).apply {
            setOnClickListener {
                activity.finish()
            }
        }

        activity.playerView.subtitleView?.apply {
            setPadding(0, 0, 0, 48)
            setStyle(
                CaptionStyleCompat(
                    Color.WHITE,
                    Color.parseColor("#88000000"),
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_NONE,
                    Color.WHITE,
                    null
                )
            )
        }

        activity.playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {
                showSystemUI(activity)
                activity.playerView.keepScreenOn = true
            } else {
                hideSystemUI(activity)
            }
        })
    }

    @OptIn(UnstableApi::class)
    private fun buildMediaItemWithSubtitles(url: String, streamingData: Streaming): MediaItem {
        val subtitleConfigs = streamingData.tracks?.mapNotNull { track ->
            if (track.file != null && track.kind == "captions") {
                MediaItem.SubtitleConfiguration.Builder(Uri.parse(track.file))
                    .setMimeType(MimeTypes.TEXT_VTT)
                    .setLanguage(track.label)
                    .setSelectionFlags(if (track.default) C.SELECTION_FLAG_DEFAULT else 0)
                    .build()
            } else {
                null
            }
        } ?: emptyList()

        val mediaItemBuilder = MediaItem.Builder()
            .setUri(url)
            .setSubtitleConfigurations(subtitleConfigs)

        return mediaItemBuilder.build()
    }

    fun hideSystemUI(activity: PlayerActivity) {
        activity.showSystemUi = false

        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    fun showSystemUI(activity: PlayerActivity) {
        activity.showSystemUi = true

        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }
}