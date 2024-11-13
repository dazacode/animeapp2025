package com.kawaidev.kawaime.network.interfaces

import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.network.callbacks.ReleaseCallback
import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.impl.AnimeServiceImpl

interface AnimeService {
    suspend fun getAnime(id: String): Release
    suspend fun searchAnime(searchParams: SearchParams): Pair<List<SearchResponse>, Boolean>
    suspend fun getHome(): Home
    suspend fun getCategory(category: String, callback: ReleaseCallback)

    companion object {
        fun create(): AnimeService {
            return AnimeServiceImpl(client = App.httpClient)
        }
    }
}