package com.kawaidev.kawaime.ui.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.interfaces.AnimeService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SearchViewModel : ViewModel() {
    private val animeService = AnimeService.create()
    private val strings = Strings

    private val _query = MutableLiveData<String>()

    private val _searchResults = MutableLiveData<MutableList<BasicRelease>>()
    val searchResults: LiveData<MutableList<BasicRelease>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _nextPage = MutableLiveData<Boolean>()
    val hasNextPage: LiveData<Boolean> get() = _nextPage

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> get() = _error

    private val _currentPage = MutableLiveData<Int>().apply { value = 1 }

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private var searchJob: Job? = null

    fun searchAnime(query: String) {
        updateCategoryEmptyState(false)
        _isLoading.postValue(true)
        searchJob?.cancel()
        _query.postValue(query)
        _currentPage.postValue(1)
        _error.postValue(null)

        executeSearch(query, 1)
    }

    fun loadNextSearch() {
        if (_isLoading.value == true) return

        _isLoading.postValue(true)
        updateCategoryEmptyState(false)

        val nextPage = (_currentPage.value ?: 1) + 1
        _currentPage.postValue(nextPage)

        val currentQuery = _query.value ?: return
        _error.postValue(null)

        executeSearch(currentQuery, nextPage)
    }

    private fun executeSearch(query: String, page: Int) {
        searchJob = viewModelScope.launch {
            try {
                val searchParams = buildSearchParams(query, page)
                val wrapper = animeService.searchAnime(searchParams)

                _isLoading.postValue(false)

                if (page == 1) {
                    _searchResults.postValue(wrapper.animes.toMutableList())
                } else {
                    val currentResults = _searchResults.value ?: mutableListOf()
                    val updatedResults = currentResults.toMutableList().apply {
                        addAll(wrapper.animes)
                    }
                    _searchResults.postValue(updatedResults)
                }

                _nextPage.postValue(wrapper.hasNextPage)
                updateCategoryEmptyState(wrapper.animes.isEmpty())

            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _error.postValue(e)
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun buildSearchParams(query: String, page: Int): SearchParams {
        val queryGenres = query.split(",")
            .map { it.trim().lowercase().replace(" ", "-") }
            .filter { it.isNotEmpty() }

        val genres = strings.genres.map { it.lowercase().replace(" ", "-") }
        val isGenreSearch = queryGenres.all { genre -> genres.contains(genre) }

        return if (isGenreSearch) {
            SearchParams(genres = queryGenres.joinToString(","), page = page)
        } else {
            SearchParams(q = query, page = page)
        }
    }

    fun refreshSearch(query: String) {
        _currentPage.postValue(1)
        _searchResults.postValue(mutableListOf())
        searchAnime(query)
    }

    private fun updateCategoryEmptyState(isEmpty: Boolean) {
        _isEmpty.postValue(isEmpty)
    }

    fun clearAnimeList() {
        _searchResults.postValue(mutableListOf())
        updateCategoryEmptyState(true)
    }
}