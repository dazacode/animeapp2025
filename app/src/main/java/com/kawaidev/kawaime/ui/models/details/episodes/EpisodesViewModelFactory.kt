package com.kawaidev.kawaime.ui.models.details.episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kawaidev.kawaime.network.interfaces.StreamingService

class EpisodesViewModelFactory(
    private val streamingService: StreamingService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodesViewModel::class.java)) {
            return EpisodesViewModel(streamingService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
