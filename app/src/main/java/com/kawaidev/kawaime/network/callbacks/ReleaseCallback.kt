package com.kawaidev.kawaime.network.callbacks

import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import java.lang.Exception

interface ReleaseCallback {
    fun onDataLoaded(animeList: List<BasicRelease>, hasNextPage: Boolean)
    fun onError(e: Exception)
}