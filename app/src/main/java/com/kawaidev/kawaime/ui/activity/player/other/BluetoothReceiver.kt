package com.kawaidev.kawaime.ui.activity.player.other

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer

class BluetoothReceiver(private val exoPlayer: ExoPlayer) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED == action) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR)
            if (state == BluetoothAdapter.STATE_DISCONNECTED) {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                }
            }
        }
    }
}