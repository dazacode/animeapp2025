package com.kawaidev.kawaime.ui.models

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer

class PlayerViewModel : ViewModel() {
    var player: ExoPlayer? = null
    var playbackPosition: Long = 0
    var playWhenReady: Boolean = true
}