package com.kawaidev.kawaime.ui.activity.player.helpers.utils

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity

object PlayerUtils {

    @androidx.annotation.OptIn(UnstableApi::class)
    fun buildMediaItemWithSubtitles(url: String, streamingData: Streaming): MediaItem {
        val subtitleConfigs = streamingData.tracks?.mapNotNull { track ->
            if (track.file != null && track.kind == "captions") {
                MediaItem.SubtitleConfiguration.Builder(Uri.parse(track.file))
                    .setMimeType(MimeTypes.TEXT_VTT)
                    .setLanguage(track.label)
                    .setSelectionFlags(if (track.default) C.SELECTION_FLAG_DEFAULT else 0)
                    .build()
            } else null
        } ?: emptyList()

        return MediaItem.Builder()
            .setUri(url)
            .setSubtitleConfigurations(subtitleConfigs)
            .build()
    }

    @SuppressLint("PrivateResource")
    fun setTint(button: ImageButton, isEnabled: Boolean) {
        button.isEnabled = isEnabled
        val color = if (isEnabled) {
            ContextCompat.getColor(button.context, androidx.media3.ui.R.color.exo_white)
        } else {
            ContextCompat.getColor(button.context, androidx.media3.ui.R.color.exo_styled_error_message_background)
        }
        button.drawable.setTint(color)
    }

    fun hideSystemUI(activity: PlayerActivity) {
        activity.showSystemUi = false

        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    fun showSystemUI(activity: PlayerActivity) {
        activity.showSystemUi = true

        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }
}
