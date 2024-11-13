package com.kawaidev.kawaime.ui.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.ui.models.PlayerViewModel
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    private var streaming: Streaming = Streaming()

    private lateinit var service: StreamingService
    private lateinit var playerViewModel: PlayerViewModel

    @State private var url: String = ""

    private var isPlayerReady = false

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Icepick.restoreInstanceState(this, savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        setContentView(R.layout.activity_player)

        service = StreamingService.create()
        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)

        streaming = Json.decodeFromString(intent.getStringExtra("streaming") ?: "")

        playerView = findViewById(R.id.player)

        if (savedInstanceState == null) {
            initializePlayer()
        } else {
            playerView.player = playerViewModel.player
            isPlayerReady = true

            playerView.subtitleView?.apply {
                setPadding(0, 0, 0, 48)
            }
        }

        playerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                playerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(playerView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }

        playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {
                showSystemUI()
            } else {
                hideSystemUI()
            }
        })

        playerView.findViewById<ImageButton>(R.id.button_back).apply {
            setOnClickListener {
                finish()
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        playerViewModel.player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        playerView.player = playerViewModel.player

        playerView.subtitleView?.apply {
            setPadding(0, 0, 0, 48)
        }

        // Fetch and set the best quality stream
        val streamingUrl = streaming.sources?.firstOrNull { it.type == "hls" }?.url

        if (streamingUrl != null) {
            // Use the StreamingService to get the best quality URL
            lifecycleScope.launch {
                try {
                    if (url.isEmpty()) url = service.getStreamingLink(streamingUrl)
                    val mediaItem = buildMediaItemWithSubtitles(url, streaming)
                    playerViewModel.player?.setMediaItem(mediaItem)
                    playerViewModel.player?.prepare()
                    playerViewModel.player?.playWhenReady = true
                } catch (e: Exception) {
                    Log.e("PlayerActivity", "Error fetching the streaming link: ${e.message}")
                }
            }
        }
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
        mediaItemBuilder.setUri(url) // Use the best quality URL here
        mediaItemBuilder.setSubtitleConfigurations(subtitleConfigs)

        return mediaItemBuilder.build()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playerViewModel.playbackPosition = savedInstanceState.getLong("playbackPosition", 0) ?: 0
        playerViewModel.playWhenReady = savedInstanceState.getBoolean("playWhenReady", true) ?: true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playerViewModel.playbackPosition)
        outState.putBoolean("playWhenReady", playerViewModel.playWhenReady)
        Icepick.saveInstanceState(this, outState)
    }
}