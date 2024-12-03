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

class SearchViewModel : ViewModel() {
    private val animeService = AnimeService.create()
    private val strings = Strings

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    private val _searchResults = MutableLiveData<MutableList<BasicRelease>>()
    val searchResults: LiveData<MutableList<BasicRelease>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _nextPage = MutableLiveData<Boolean>()
    val hasNextPage: LiveData<Boolean> get() = _nextPage

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> get() = _error

    private val _currentPage = MutableLiveData<Int>().apply { value = 1 }
    val currentPage: LiveData<Int> get() = _currentPage

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private var searchJob: Job? = null

    fun searchAnime(query: String) {
        updateCategoryEmptyState(false)

        _isLoading.postValue(true)
        _nextPage.postValue(true)
        searchJob?.cancel()
        _query.postValue(query)
        _currentPage.postValue(1)
        _error.postValue(null)

        viewModelScope.launch {
            try {
                val queryGenres = query.split(",")
                    .map { it.trim().lowercase().replace(" ", "-") }
                    .filter { it.isNotEmpty() }

                val genres = strings.genres.map { it.lowercase().replace(" ", "-") }
                val isGenreSearch = queryGenres.all { genre -> genres.contains(genre) }

                val searchParams = if (isGenreSearch) {
                    SearchParams(genres = queryGenres.joinToString(","))
                } else {
                    SearchParams(q = query)
                }

                val (animeList, hasNextPage) = animeService.searchAnime(searchParams)
                _isLoading.postValue(false)

                _searchResults.postValue(animeList.toMutableList())
                _nextPage.postValue(hasNextPage)

                if (animeList.isEmpty()) updateCategoryEmptyState(true)

            } catch (e: Exception) {
                _error.postValue(e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun loadNextSearch() {
        if (_isLoading.value == true) return

        _isLoading.postValue(true)

        updateCategoryEmptyState(false)

        val nextPage = (_currentPage.value ?: 1) + 1
        _currentPage.postValue(nextPage)

        val currentQuery = _query.value ?: return
        _error.postValue(null)

        viewModelScope.launch {
            try {
                val queryGenres = currentQuery.split(",")
                    .map { it.trim().lowercase() }
                    .filter { it.isNotEmpty() }

                val genres = strings.genres.map { it.lowercase() }
                val isGenreSearch = queryGenres.all { genre -> genres.contains(genre) }

                val searchParams = if (isGenreSearch) {
                    SearchParams(genres = queryGenres.joinToString(","), page = nextPage)
                } else {
                    SearchParams(q = currentQuery, page = nextPage)
                }

                val (animeList, hasNextPage) = animeService.searchAnime(searchParams)

                _isLoading.postValue(false)

                val currentResults = _searchResults.value ?: mutableListOf()
                val updatedResults = currentResults.toMutableList().apply {
                    addAll(animeList)
                }

                if (updatedResults.isEmpty()) {
                    updateCategoryEmptyState(true)
                } else {
                    updateCategoryEmptyState(false)
                }

                _searchResults.postValue(updatedResults)
                _nextPage.postValue(hasNextPage)
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _error.postValue(e)
            } finally {
                _isLoading.postValue(false)
            }
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