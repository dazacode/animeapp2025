package com.kawaidev.kawaime.ui.activity.player.helpers.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity

class PlayerLifecycleObserver(
    private val activity: PlayerActivity
) : DefaultLifecycleObserver {

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        // Only pause if the activity is not in PiP mode
        if (!activity.isInPictureInPictureMode) {
            activity.save()  // Save the state
            activity.playerViewModel.player?.pause()  // Pause the player
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        // Only release player if not in PiP mode
        if (!activity.isInPictureInPictureMode) {
            activity.save()  // Save the state
            activity.playerViewModel.player?.release()
            activity.playerViewModel.player = null

            activity.unregisterReceiver(activity.pipActionReceiver)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        // Resume the player only if not in PiP mode
        if (!activity.isInPictureInPictureMode) {
            when(activity.playerViewModel.playWhenReady) {
                true -> activity.playerViewModel.player?.play()
                false -> activity.playerViewModel.player?.pause()
            }
        }
    }
}