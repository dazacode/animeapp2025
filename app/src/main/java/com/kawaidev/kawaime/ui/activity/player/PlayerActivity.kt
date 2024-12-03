package com.kawaidev.kawaime.ui.activity.player

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.helpers.WatchedRelease
import com.kawaidev.kawaime.network.dao.streaming.EpisodeDetail
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.ui.activity.player.helpers.PlayerHelper
import com.kawaidev.kawaime.ui.activity.player.helpers.PlayerLifecycleObserver
import com.kawaidev.kawaime.ui.models.PlayerViewModel
import icepick.Icepick
import icepick.State
import kotlinx.serialization.json.Json


class PlayerActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    lateinit var playerView: PlayerView
    lateinit var prefs: Prefs

    var streaming: Streaming = Streaming()
    var id: String = ""
    var name: String = ""
    var episode: String = ""
    var episodes: Episodes = Episodes()
    var quality: String = ""
    var server: String = ""
    var category: String = ""
    private var download: Boolean = false

    lateinit var service: StreamingService
    lateinit var playerViewModel: PlayerViewModel

    @State var url: String = ""
    @State var showSystemUi = true

    private var isPlayerReady = false

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        player = ExoPlayer.Builder(this).build()

        setContentView(R.layout.activity_player)

        service = StreamingService.create()
        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
        prefs = App.prefs

        streaming = Json.decodeFromString(intent.getStringExtra("streaming") ?: "")
        episodes = Json.decodeFromString(intent.getStringExtra("episodes") ?: "")
        id = intent.getStringExtra("id") ?: ""
        name = intent.getStringExtra("name") ?: ""
        episode = intent.getStringExtra("episode") ?: ""
        quality = intent.getStringExtra("quality") ?: ""
        server = intent.getStringExtra("server") ?: ""
        category = intent.getStringExtra("category") ?: ""

        download = quality.isNotEmpty()

        playerView = findViewById(R.id.player)

        initialize(savedInstanceState)

        lifecycle.addObserver(PlayerLifecycleObserver(this))
    }

    @OptIn(UnstableApi::class)
    private fun initialize(savedInstanceState: Bundle?) {
        if (!download) {
            if (savedInstanceState == null || playerViewModel.player?.isReleased == true) {
                PlayerHelper.initializePlayer(this)
            } else {
                isPlayerReady = true
            }

            PlayerHelper.initializePlayerUI(this)
        } else {
            PlayerHelper.startDownload(this)
        }
    }

    fun save() {
        val currentPosition = playerViewModel.player?.currentPosition ?: 0L
        if (currentPosition > 0L) {
            prefs.saveWatchedRelease(WatchedRelease(episodeId = id, watchedTo = currentPosition))
            playerViewModel.playWhenReady = playerViewModel.player?.playWhenReady ?: false
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playerViewModel.playbackPosition = savedInstanceState.getLong("playbackPosition", 0)
        playerViewModel.playWhenReady = savedInstanceState.getBoolean("playWhenReady", true)
        Icepick.restoreInstanceState(this, savedInstanceState)

        if (showSystemUi) PlayerHelper.showSystemUI(this) else PlayerHelper.hideSystemUI(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playerViewModel.playbackPosition)
        outState.putBoolean("playWhenReady", playerViewModel.playWhenReady)
        Icepick.saveInstanceState(this, outState)
    }
}