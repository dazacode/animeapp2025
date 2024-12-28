package com.kawaidev.kawaime.ui.activity.player.helpers.playerUI

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.player.PlayerActivity
import com.kawaidev.kawaime.ui.activity.player.helpers.PiP.PiPHelper
import com.kawaidev.kawaime.ui.activity.player.helpers.navigation.PlayerNavigationHelper
import com.kawaidev.kawaime.ui.activity.player.helpers.utils.PlayerUtils
import com.kawaidev.kawaime.utils.Converts
import kotlin.math.max

object PlayerUIHelper {

    fun initializePlayerUI(activity: PlayerActivity) {
        activity.playerView.player = activity.playerViewModel.player
        setupWindowInsets(activity)
        setupControllerButtons(activity)
        setupSubtitleStyle(activity)
        setupLoadingIndicators(activity)
        setupUi(activity)
    }

    private fun setupWindowInsets(activity: PlayerActivity) {
        ViewCompat.setOnApplyWindowInsetsListener(activity.playerView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val padding = Converts.dpToPx(8f, activity).toInt()

            v.setPadding(max(insets.left, insets.right), 0, max(insets.left, insets.right), insets.bottom)
            activity.playerView.findViewById<RelativeLayout>(R.id.top_appbar)
                .setPaddingRelative(padding, insets.top, padding, insets.top)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupControllerButtons(activity: PlayerActivity) {
        activity.playerView.findViewById<ImageButton>(R.id.button_back).setOnClickListener { activity.finish() }
        activity.playerView.findViewById<ImageButton>(R.id.PiP).setOnClickListener { PiPHelper.enterPiPMode(activity) }
        activity.playerView.findViewById<ImageButton>(R.id.skip).setOnClickListener {
            PlayerNavigationHelper.seekForward(activity)
        }
        activity.playerView.findViewById<ImageButton>(R.id.next).apply {
            setOnClickListener { PlayerNavigationHelper.playNextEpisode(activity) }
            PlayerUtils.setTint(this, PlayerNavigationHelper.hasNextEpisode(activity))
        }
        activity.playerView.findViewById<ImageButton>(R.id.prev).apply {
            setOnClickListener { PlayerNavigationHelper.playPreviousEpisode(activity) }
            PlayerUtils.setTint(this, PlayerNavigationHelper.hasPreviousEpisode(activity))
        }

        activity.playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {
                PlayerUtils.showSystemUI(activity)
                activity.playerView.keepScreenOn = true
            } else {
                PlayerUtils.hideSystemUI(activity)
            }
        })
    }

    @OptIn(UnstableApi::class)
    private fun setupSubtitleStyle(activity: PlayerActivity) {
        activity.playerView.subtitleView?.apply {
            setPadding(0, 0, 0, Converts.dpToPx(24f, activity).toInt())
            setStyle(
                CaptionStyleCompat(
                    Color.WHITE,
                    Color.parseColor("#88000000"),
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_NONE,
                    Color.WHITE,
                    null
                )
            )
        }
    }

    @OptIn(UnstableApi::class)
    private fun setupLoadingIndicators(activity: PlayerActivity) {
        val loadingIndicator: ProgressBar = activity.playerView.findViewById(R.id.loading)
        val playPause: ImageButton = activity.playerView.findViewById(androidx.media3.ui.R.id.exo_play_pause)

        activity.playerViewModel.player?.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        playPause.visibility = View.GONE
                        loadingIndicator.visibility = View.VISIBLE
                    }
                    Player.STATE_READY, Player.STATE_ENDED, Player.STATE_IDLE -> {
                        playPause.visibility = View.VISIBLE
                        loadingIndicator.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setupUi(activity: PlayerActivity) {
        activity.playerView.findViewById<TextView>(R.id.title).text = activity.params.title
        activity.playerView.findViewById<TextView>(R.id.episode).text = activity.getString(R.string.episode, activity.params.episode)
    }
}
