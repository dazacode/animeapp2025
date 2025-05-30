package com.kawaidev.kawaime.ui.activity.player

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
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
import com.kawaidev.kawaime.network.dao.helpers.PlayerParams
import com.kawaidev.kawaime.network.dao.helpers.WatchedRelease
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.ui.activity.player.helpers.PiP.PiPActionReceiver
import com.kawaidev.kawaime.ui.activity.player.helpers.PiP.PiPHelper
import com.kawaidev.kawaime.ui.activity.player.helpers.PlayerHelper
import com.kawaidev.kawaime.ui.activity.player.helpers.lifecycle.PlayerLifecycleObserver
import com.kawaidev.kawaime.ui.activity.player.helpers.playerUI.PlayerUIHelper
import com.kawaidev.kawaime.ui.activity.player.helpers.utils.PlayerUtils
import com.kawaidev.kawaime.ui.models.PlayerViewModel
// import icepick.Icepick  // Temporarily disabled for hot reload
// import icepick.State    // Temporarily disabled for hot reload
import kotlinx.serialization.json.Json


class PlayerActivity : AppCompatActivity() {
    lateinit var pipActionReceiver: PiPActionReceiver
    private lateinit var player: ExoPlayer
    lateinit var playerView: PlayerView
    lateinit var prefs: Prefs

    var streaming: Streaming = Streaming()
    var params: PlayerParams = PlayerParams()

    lateinit var service: StreamingService
    lateinit var playerViewModel: PlayerViewModel

    // @JvmField @State var url: String = ""  // Temporarily disabled for hot reload
    // @JvmField @State var showSystemUi = true  // Temporarily disabled for hot reload
    var url: String = ""
    var showSystemUi = true

    private var isPlayerReady = false

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        player = ExoPlayer.Builder(this).build()

        setContentView(R.layout.activity_player)

        service = StreamingService.create()
        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
        prefs = App.prefs

        streaming = Json.decodeFromString(intent.getStringExtra("STREAMING") ?: "")
        params = Json.decodeFromString(intent.getStringExtra("params") ?: "")

        playerView = findViewById(R.id.player)

        initialize(savedInstanceState)

        lifecycle.addObserver(PlayerLifecycleObserver(this))

        pipActionReceiver = PiPActionReceiver()
        val filter = IntentFilter().apply {
            addAction("PLAY_PAUSE")
            addAction("PREVIOUS")
            addAction("NEXT")
        }
        registerReceiver(pipActionReceiver, filter)
    }

    @OptIn(UnstableApi::class)
    private fun initialize(savedInstanceState: Bundle?) {
        if (savedInstanceState == null || playerViewModel.player?.isReleased == true) {
            PlayerHelper.initializePlayer(this)
        } else {
            isPlayerReady = true
        }

        PlayerUIHelper.initializePlayerUI(this)
    }

    fun save() {
        val currentPosition = playerViewModel.player?.currentPosition ?: 0L
        if (currentPosition > 0L) {
            prefs.saveWatchedRelease(WatchedRelease(episodeId = params.animeEpisodeId, watchedTo = currentPosition))
            playerViewModel.playWhenReady = playerViewModel.player?.playWhenReady ?: false
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playerViewModel.playbackPosition = savedInstanceState.getLong("playbackPosition", 0)
        playerViewModel.playWhenReady = savedInstanceState.getBoolean("playWhenReady", true)
        // Icepick.restoreInstanceState(this, savedInstanceState)

        if (showSystemUi) PlayerUtils.showSystemUI(this) else PlayerUtils.hideSystemUI(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playerViewModel.playbackPosition)
        outState.putBoolean("playWhenReady", playerViewModel.playWhenReady)
        // Icepick.saveInstanceState(this, outState)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            PiPHelper.enterPiPMode(this)
        } else {
            if (isFinishing) finish()

            PiPHelper.onPiPClosed(this)
        }
    }
}