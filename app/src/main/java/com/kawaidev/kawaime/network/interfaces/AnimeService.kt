package com.kawaidev.kawaime.network.interfaces

import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.Schedule
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.dao.wrappers.SearchWrapper
import com.kawaidev.kawaime.network.impl.AnimeServiceImpl
import java.time.LocalDate

interface AnimeService {
    suspend fun getAnime(id: String): Release
    suspend fun searchAnime(searchParams: SearchParams): SearchWrapper
    suspend fun getHome(): Home
    suspend fun getCategory(category: String, page: Int = 1): SearchWrapper
    suspend fun getSchedule(date: LocalDate): List<Schedule>

    companion object {
        fun create(): AnimeService {
            return AnimeServiceImpl()
        }
    }
}