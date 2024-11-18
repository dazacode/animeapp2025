package com.kawaidev.kawaime.ui.activity.player

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
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
import com.kawaidev.kawaime.network.dao.helpers.WatchedRelease
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.ui.activity.player.helpers.BluetoothReceiver
import com.kawaidev.kawaime.ui.activity.player.helpers.HeadphoneReceiver
import com.kawaidev.kawaime.ui.activity.player.helpers.MediaReceiver
import com.kawaidev.kawaime.ui.activity.player.helpers.PlayerHelper
import com.kawaidev.kawaime.ui.models.PlayerViewModel
import icepick.Icepick
import icepick.State
import kotlinx.serialization.json.Json


class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    lateinit var playerView: PlayerView
    lateinit var prefs: Prefs

    private lateinit var headphoneReceiver: HeadphoneReceiver
    private lateinit var bluetoothReceiver: BluetoothReceiver
    private lateinit var mediaSession: MediaSession
    private lateinit var mediaButtonReceiver: MediaReceiver

    var streaming: Streaming = Streaming()
    var id: String = ""

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
        id = intent.getStringExtra("id") ?: ""

        playerView = findViewById(R.id.player)

        if (savedInstanceState == null) {
            PlayerHelper.initializePlayer(this)
        } else {
            isPlayerReady = true
        }

        PlayerHelper.initializePlayerUI(this)
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

    override fun onPause() {
        super.onPause()
        val currentPosition = playerViewModel.player?.currentPosition ?: 0L
        if (currentPosition > 0L) {
            prefs.saveWatchedRelease(WatchedRelease(episodeId = id, watchedTo = currentPosition))
            playerViewModel.playWhenReady = playerViewModel.player?.playWhenReady ?: false
            playerViewModel.player?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        when(playerViewModel.playWhenReady) {
            true -> playerViewModel.player?.play()
            false -> playerViewModel.player?.pause()
        }
    }
}