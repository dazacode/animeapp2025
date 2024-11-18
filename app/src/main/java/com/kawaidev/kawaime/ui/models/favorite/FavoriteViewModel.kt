package com.kawaidev.kawaime.ui.models.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.interfaces.AnimeService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FavoriteViewModel(private val service: AnimeService, private val prefs: Prefs) : ViewModel() {

    val anime = MutableLiveData<List<SearchResponse>>()
    val isLoading = MutableLiveData<Boolean>()
    val isRefreshing = MutableLiveData<Boolean>()
    val isEmpty = MutableLiveData<Boolean>()
    val hasNextPage = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()
    val page = MutableLiveData<Int>()

    private val perPage = 25

    init {
        page.value = 1
        anime.value = emptyList()
    }

    fun getAnime(isRefresh: Boolean = false, isReload: Boolean = false) {
        if (isRefresh || isReload) {
            page.value = 1
        }

        if (isRefresh) isRefreshing.value = true else isLoading.value = true
        error.value = null

        viewModelScope.launch {
            try {
                val ids = prefs.getFavorites().toList()
                val totalItems = ids.size
                val start = (page.value!! - 1) * perPage
                val end = (start + perPage).coerceAtMost(totalItems)
                val currentIds = ids.subList(start, end)

                val animePage = currentIds.map { id ->
                    async {
                        try {
                            val release = service.getAnime(id)
                            convertReleaseToSearchResponse(release)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll()

                val newAnime = animePage.filterNotNull()

                if (isRefresh || isReload) {
                    anime.value = newAnime
                } else {
                    anime.value = anime.value!! + newAnime
                }

                isEmpty.value = anime.value.isNullOrEmpty()
                hasNextPage.value = end < totalItems
                page.value = page.value!! + 1
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                if (isRefresh) isRefreshing.value = false else isLoading.value = false
            }
        }
    }

    private fun convertReleaseToSearchResponse(release: Release): SearchResponse {
        val animeInfo = release.anime?.info ?: return SearchResponse()
        return SearchResponse(
            id = animeInfo.id,
            name = animeInfo.name,
            description = animeInfo.description,
            poster = animeInfo.poster,
            episodes = animeInfo.stats?.episodes,
            type = animeInfo.stats?.type,
            rating = animeInfo.stats?.rating
        )
    }
}