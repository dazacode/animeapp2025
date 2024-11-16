package com.kawaidev.kawaime.ui.activity.player.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.media3.exoplayer.ExoPlayer

class HeadphoneReceiver(private val exoPlayer: ExoPlayer) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
        }
    }
}