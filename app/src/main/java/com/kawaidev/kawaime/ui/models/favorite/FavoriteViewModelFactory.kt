package com.kawaidev.kawaime.ui.models.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.network.interfaces.AnimeService

class FavoriteViewModelFactory(private val service: AnimeService, private val prefs: Prefs) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(service, prefs) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}