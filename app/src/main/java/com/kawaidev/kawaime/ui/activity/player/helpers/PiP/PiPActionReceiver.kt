package com.kawaidev.kawaime.ui.activity.player.helpers.PiP

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import com.kawaidev.kawaime.ui.activity.player.helpers.PlayerPlayingHelper

class PiPActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (context is PlayerActivity) {
            when (intent.action) {
                "PLAY_PAUSE" -> {
                    if (context.playerViewModel.player?.isPlaying == true) {
                        context.playerViewModel.player?.pause()
                        PiPHelper.updatePlayPauseAction(context, false)
                    } else {
                        context.playerViewModel.player?.play()
                        PiPHelper.updatePlayPauseAction(context, true)
                    }
                }
                "PREVIOUS" -> PlayerPlayingHelper.playPreviousEpisode(context)
                "NEXT" -> PlayerPlayingHelper.playNextEpisode(context)
            }
        }
    }
}