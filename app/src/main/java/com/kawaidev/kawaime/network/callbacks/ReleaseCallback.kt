package com.kawaidev.kawaime.network.callbacks

import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import java.lang.Exception

interface ReleaseCallback {
    fun onDataLoaded(animeList: List<SearchResponse>, hasNextPage: Boolean)
    fun onError(e: Exception)
}