package com.kawaidev.kawaime.ui.models.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.interfaces.AnimeService
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel() {
    private val service: AnimeService = AnimeService.create()

    private val _release = MutableLiveData<Release>()
    val release: LiveData<Release> get() = _release

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefresh = MutableLiveData<Boolean>()
    val isRefresh: LiveData<Boolean> get() = _isRefresh

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchAnime(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) _isRefresh.value = true else _isLoading.value = true
            _error.value = null
            try {
                val release = service.getAnime(id)
                _release.value = release
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                if (isRefresh) _isRefresh.value = false else _isLoading.value = false
            }
        }
    }
}