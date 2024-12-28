package com.kawaidev.kawaime.ui.activity.player.helpers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
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
import com.kawaidev.kawaime.ui.activity.player.helpers.PiP.PiPHelper
import com.kawaidev.kawaime.utils.Converts
import kotlinx.coroutines.launch
import kotlin.math.max


object PlayerHelper {

    @OptIn(UnstableApi::class)
    fun initializePlayer(activity: PlayerActivity) {
        val trackSelector = DefaultTrackSelector(activity)

        if (activity.playerViewModel.player == null) {
            activity.playerViewModel.player = ExoPlayer.Builder(activity)
                .setTrackSelector(trackSelector)
                .build()
        }

        val streamingUrl = activity.streaming.sources?.firstOrNull { it.type == "hls" }?.url

        if (streamingUrl != null) {
            activity.url = streamingUrl

            activity.lifecycleScope.launch {
                try {
                    val mediaItem = buildMediaItemWithSubtitles(activity.url, activity.streaming)

                    activity.playerViewModel.player?.clearMediaItems()
                    activity.playerViewModel.player?.setMediaItem(mediaItem)
                    activity.playerViewModel.player?.prepare()

                    val wasPlaying = activity.playerViewModel.playWhenReady
                    activity.playerViewModel.player?.setHandleAudioBecomingNoisy(true)

                    val watched = activity.prefs.findByEpisodeId(activity.params.animeEpisodeId)
                    if (watched != null) {
                        activity.playerViewModel.player?.seekTo(watched.watchedTo)
                    }

                    activity.playerViewModel.player?.addListener(object : Player.Listener {
                        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                            if (playbackState == Player.STATE_READY) {
                                setTint(activity.playerView.findViewById(R.id.skip), true)

                                activity.playerViewModel.player?.removeListener(this)

                                if (activity.playerViewModel.player?.isPlaying == false) {
                                    activity.playerViewModel.player?.playWhenReady = wasPlaying
                                } else {
                                    activity.playerViewModel.player?.playWhenReady = playWhenReady
                                }
                            } else {
                                setTint(activity.playerView.findViewById(R.id.skip), false)
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
            val horizontalPadding = max(insets.left, insets.right)
            val verticalPadding = insets.top
            val padding = Converts.dpToPx(8f, activity).toInt()

            v.setPadding(horizontalPadding, 0, horizontalPadding, insets.bottom)

            activity.playerView.findViewById<RelativeLayout>(R.id.top_appbar)
                .setPaddingRelative(padding, verticalPadding, padding, verticalPadding)

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

        activity.playerView.findViewById<ImageButton>(R.id.PiP).apply {
            setOnClickListener {
                PiPHelper.enterPiPMode(activity)
            }
        }

        activity.playerView.findViewById<ImageButton>(R.id.skip).apply {
            setOnClickListener {
                val currentPosition = activity.playerViewModel.player?.currentPosition ?: 0L
                val seekForwardPosition = currentPosition + 85000
                activity.playerViewModel.player?.seekTo(seekForwardPosition)
            }
        }

        activity.playerView.findViewById<ImageButton>(R.id.button_download).apply {
            setOnClickListener {
                val qualities = mapOf(
                    "360p" to activity.url.replace("master", "index-f3-v1-a1"),
                    "720p" to activity.url.replace("master", "index-f2-v1-a1"),
                    "1080p" to activity.url.replace("master", "index-f1-v1-a1"),
                )

                val qualityKeys = qualities.keys.toTypedArray()

                AlertDialog.Builder(activity)
                    .setTitle("Choose Quality")
                    .setItems(qualityKeys) { _, which ->
                        val selectedQuality = qualityKeys[which]
                        val selectedUrl = qualities[selectedQuality]

                        if (selectedUrl != null) {
                            DownloadHelper.startDownload(activity, selectedUrl)
                        } else {
                            Toast.makeText(activity, "Error selecting quality", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        activity.playerView.findViewById<ImageButton>(R.id.next).apply {
            setOnClickListener {
                PlayerPlayingHelper.playNextEpisode(activity)
            }

            isEnabled = hasNextEpisode(activity)

            setTint(activity.playerView.findViewById(R.id.next), isEnabled)
        }

        activity.playerView.findViewById<ImageButton>(R.id.prev).apply {
            setOnClickListener {
                PlayerPlayingHelper.playPreviousEpisode(activity)
            }

            isEnabled = hasPreviousEpisode(activity)

            setTint(activity.playerView.findViewById(R.id.prev), isEnabled)
        }


        activity.playerView.findViewById<TextView>(R.id.title).text = activity.params.title
        activity.playerView.findViewById<TextView>(R.id.episode).text = activity.getString(R.string.episode, activity.params.episode)

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

        val loadingIndicator: ProgressBar = activity.playerView.findViewById(R.id.loading)
        val playPause: ImageButton = activity.playerView.findViewById(androidx.media3.ui.R.id.exo_play_pause)

        activity.playerViewModel.player?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        playPause.visibility = View.GONE
                        loadingIndicator.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        playPause.visibility = View.VISIBLE
                        loadingIndicator.visibility = View.GONE
                    }
                    Player.STATE_ENDED -> {
                        playPause.visibility = View.VISIBLE
                        loadingIndicator.visibility = View.GONE
                    }
                    Player.STATE_IDLE -> {
                        playPause.visibility = View.VISIBLE
                        loadingIndicator.visibility = View.GONE
                    }
                }
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

    private fun getCurrentEpisodeIndex(activity: PlayerActivity): Int {
        return activity.params.episodes.episodes?.indexOfFirst { it.episodeId == activity.params.animeEpisodeId } ?: -1
    }

    /**
     * Returns true if there is a next episode available.
     */
    fun hasNextEpisode(activity: PlayerActivity): Boolean {
        val currentIndex = getCurrentEpisodeIndex(activity)
        return currentIndex < (activity.params.episodes.episodes?.size ?: 0) - 1
    }

    /**
     * Returns true if there is a previous episode available.
     */
    fun hasPreviousEpisode(activity: PlayerActivity): Boolean {
        val currentIndex = getCurrentEpisodeIndex(activity)
        return currentIndex > 0
    }

    @SuppressLint("PrivateResource")
    private fun setTint(button: ImageButton, isEnabled: Boolean) {
        button.isEnabled = isEnabled

        val color = if (isEnabled) {
            ContextCompat.getColor(button.context, androidx.media3.ui.R.color.exo_white)
        } else {
            ContextCompat.getColor(button.context, androidx.media3.ui.R.color.exo_styled_error_message_background)
        }

        button.drawable.setTint(color)
    }
}