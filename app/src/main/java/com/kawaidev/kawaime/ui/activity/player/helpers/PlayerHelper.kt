package com.kawaidev.kawaime.ui.activity.player.helpers

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
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

                    activity.playerViewModel.player?.addListener(object : Player.Listener {
                        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                            if (playbackState == Player.STATE_READY) {
                                setTint(activity.playerView.findViewById(R.id.skip), true)

                                val watched = activity.prefs.findByEpisodeId(activity.id)
                                if (watched != null) {
                                    activity.playerViewModel.player?.seekTo(watched.watchedTo)
                                }

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
            val horizontalMargin = max(insets.left, insets.right)
            val verticalMargin = insets.top

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = horizontalMargin
                rightMargin = horizontalMargin
                bottomMargin = insets.bottom
            }

            activity.playerView.findViewById<LinearLayout>(R.id.top_appbar).setPaddingRelative(8, verticalMargin, 8, verticalMargin)

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

        activity.playerView.findViewById<ImageButton>(R.id.skip).apply {
            setOnClickListener {
                val currentPosition = activity.playerViewModel.player?.currentPosition ?: 0L
                val seekForwardPosition = currentPosition + 85000
                activity.playerViewModel.player?.seekTo(seekForwardPosition)
            }
        }

        val currentIndex = getCurrentEpisodeIndex(activity)

        activity.playerView.findViewById<ImageButton>(R.id.next).apply {
            setOnClickListener {
                PlayerPlayingHelper.playNextEpisode(activity)
            }

            isEnabled = currentIndex < (activity.episodes.episodes?.size ?: 0) - 1

            setTint(activity.playerView.findViewById(R.id.next), isEnabled)
        }

        activity.playerView.findViewById<ImageButton>(R.id.prev).apply {
            setOnClickListener {
                PlayerPlayingHelper.playPreviousEpisode(activity)
            }

            isEnabled = currentIndex > 0

            setTint(activity.playerView.findViewById(R.id.prev), isEnabled)
        }


        activity.playerView.findViewById<TextView>(R.id.title).text = activity.name
        activity.playerView.findViewById<TextView>(R.id.episode).text = activity.getString(R.string.episode, activity.episode)

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

    @OptIn(UnstableApi::class)
    fun startDownload(activity: PlayerActivity) {
        val qualityMap = mapOf(
            "1080p" to "index-f1-v1-a1.m3u8",
            "720p" to "index-f2-v1-a1.m3u8",
            "360p" to "index-f3-v1-a1.m3u8"
        )

        var uri: Uri = Uri.EMPTY
        val sourceUrl = activity.streaming.sources?.firstOrNull()?.url
        if (sourceUrl != null) {
            val updatedUrl = sourceUrl.replace("master.m3u8", qualityMap[activity.quality] ?: "master.m3u8")
            uri = Uri.parse(updatedUrl)
        }

        val subtitleTrack = activity.streaming.tracks?.find { it.default }
        val subtitleUri = subtitleTrack?.file?.let { Uri.parse(it) }

        Log.d("DownloadRequest", "URL: $uri")
        Log.d("DownloadRequest", "Quality: ${activity.quality}")

        val downloadRequestBuilder = DownloadRequest.Builder(activity.id, uri)
            .setMimeType(MimeTypes.APPLICATION_M3U8)

        subtitleUri?.let {
            val subtitleStreamKey = StreamKey(C.TRACK_TYPE_TEXT, 0, 0)
            downloadRequestBuilder.setStreamKeys(listOf(subtitleStreamKey))
        }

        val downloadRequest = downloadRequestBuilder.build()

        val downloadManager = com.kawaidev.kawaime.ui.activity.player.helpers.DownloadManager.getInstance(activity)

        downloadManager.addListener(object : androidx.media3.exoplayer.offline.DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: androidx.media3.exoplayer.offline.DownloadManager,
                download: Download,
                finalException: Exception?
            ) {
                when (download.state) {
                    Download.STATE_DOWNLOADING -> {
                        activity.finish()
                    }
                }
            }
        })

        downloadManager.addDownload(downloadRequest)

        DownloadService.sendAddDownload(
            activity,
            VideoDownloadService::class.java,
            downloadRequest,
            true
        )
    }

    private fun getCurrentEpisodeIndex(activity: PlayerActivity): Int {
        return activity.episodes.episodes?.indexOfFirst { it.episodeId == activity.id } ?: -1
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