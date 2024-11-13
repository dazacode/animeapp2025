package com.kawaidev.kawaime.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.interfaces.AnimeService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var home: Home = Home()
    var isFetched: Boolean = false

    fun fetchHomeData(service: AnimeService, onResult: (Home?) -> Unit) {
        if (!isFetched) {
            viewModelScope.launch {
                try {
                    val fetchedHome = service.getHome()
                    home = fetchedHome
                    isFetched = true
                    onResult(fetchedHome)
                } catch (e: Exception) {
                    onResult(null)
                }
            }
        } else {
            onResult(home)
        }
    }
}