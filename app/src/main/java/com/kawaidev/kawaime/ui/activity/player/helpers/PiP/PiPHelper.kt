package com.kawaidev.kawaime.ui.activity.player.helpers.PiP

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Rational
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity

object PiPHelper {

    @OptIn(UnstableApi::class)
    fun enterPiPMode(activity: PlayerActivity) {
        activity.playerView.useController = false

        val aspectRatio = Rational(16, 9)
        val pictureInPictureParams = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .build()
        activity.enterPictureInPictureMode(pictureInPictureParams)
        updatePlayPauseAction(activity, activity.playerViewModel.player?.isPlaying == true)
    }

    @OptIn(UnstableApi::class)
    fun updatePlayPauseAction(activity: PlayerActivity, isPlaying: Boolean) {
        val playAction = createPlayAction(activity)
        val pauseAction = createPauseAction(activity)
        val previousAction = createPreviousAction(activity)
        val nextAction = createNextAction(activity)

        val actions = listOf(previousAction, if (isPlaying) pauseAction else playAction, nextAction)

        val pictureInPictureParamsBuilder = PictureInPictureParams.Builder()
        val aspectRatio = Rational(16, 9)
        pictureInPictureParamsBuilder.setAspectRatio(aspectRatio)
        pictureInPictureParamsBuilder.setActions(actions)

        activity.setPictureInPictureParams(pictureInPictureParamsBuilder.build())
    }

    private fun createPlayAction(activity: PlayerActivity): RemoteAction {
        val icon = Icon.createWithResource(activity, R.drawable.play_outlined)
        val intent = Intent("PLAY_PAUSE")
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return RemoteAction(icon, "Play", "Play", pendingIntent)
    }

    private fun createPauseAction(activity: PlayerActivity): RemoteAction {
        val icon = Icon.createWithResource(activity, R.drawable.pause_outlined)
        val intent = Intent("PLAY_PAUSE")
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return RemoteAction(icon, "Pause", "Pause", pendingIntent)
    }

    private fun createPreviousAction(activity: PlayerActivity): RemoteAction {
        val icon = Icon.createWithResource(activity, androidx.media3.session.R.drawable.media3_icon_previous)
        val intent = Intent("PREVIOUS")
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return RemoteAction(icon, "Previous", "Skip to previous track", pendingIntent)
    }

    private fun createNextAction(activity: PlayerActivity): RemoteAction {
        val icon = Icon.createWithResource(activity, androidx.media3.session.R.drawable.media3_icon_next)
        val intent = Intent("NEXT")
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return RemoteAction(icon, "Next", "Skip to next track", pendingIntent)
    }

    @OptIn(UnstableApi::class)
    fun onPiPClosed(activity: PlayerActivity) {
        activity.playerView.useController = true
    }
}