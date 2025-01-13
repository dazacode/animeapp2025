package com.kawaidev.kawaime.network.impl

import com.kawaidev.kawaime.network.Request
import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.Schedule
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.dao.wrappers.SearchWrapper
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.network.routes.AnimeRoutes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnimeServiceImpl : AnimeService {

    private val routes = AnimeRoutes

    override suspend fun getAnime(id: String): Release {
        val url = "${routes.INFO}$id"
        return Request.getRequest(url, "data")
    }

    override suspend fun searchAnime(searchParams: SearchParams): SearchWrapper {
        val url = searchParams.buildSearchUrl(routes.SEARCH)
        return Request.getRequest(url, "data")
    }

    override suspend fun getHome(): Home {
        val url = routes.SPOTLIGHT
        return Request.getRequest(url, "data")
    }

    override suspend fun getCategory(category: String, page: Int): SearchWrapper {
        val url = "${routes.CATEGORY}$category?page=$page"
        return Request.getRequest(url, "data")
    }

    override suspend fun getSchedule(date: LocalDate): List<Schedule> {
        val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val url = "${routes.SCHEDULE}$formattedDate"
        return Request.getRequest(url, "data.scheduledAnimes")
    }
}