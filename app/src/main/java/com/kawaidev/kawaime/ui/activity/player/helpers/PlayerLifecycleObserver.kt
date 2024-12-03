package com.kawaidev.kawaime.ui.activity.player.helpers

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity

class PlayerLifecycleObserver(
    private val activity: PlayerActivity
) : DefaultLifecycleObserver {

    override fun onPause(owner: LifecycleOwner) {
        activity.save()

        activity.playerViewModel.player?.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        activity.save()

        activity.playerViewModel.player?.release()
        activity.playerViewModel.player = null
    }

    override fun onResume(owner: LifecycleOwner) {
        when(activity.playerViewModel.playWhenReady) {
            true -> activity.playerViewModel.player?.play()
            false -> activity.playerViewModel.player?.pause()
        }
    }
}
