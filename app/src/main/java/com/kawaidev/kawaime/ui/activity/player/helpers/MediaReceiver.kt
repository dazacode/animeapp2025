package com.kawaidev.kawaime.ui.activity.player.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import androidx.media3.exoplayer.ExoPlayer

class MediaReceiver(private val exoPlayer: ExoPlayer) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_MEDIA_BUTTON == action) {
            val event: KeyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)!!
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    }
                    KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        exoPlayer.play()
                    }
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        exoPlayer.pause()
                    }
                    else -> {
                    }
                }
            }
        }
    }
}