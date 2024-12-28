package com.kawaidev.kawaime.network.impl

import com.kawaidev.kawaime.network.Request
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.network.interfaces.NewsService
import com.kawaidev.kawaime.network.routes.NewsRoutes

class NewsServiceImpl : NewsService {

    private val routes = NewsRoutes

    override suspend fun getRecentNews(): List<News> {
        val url = routes.RECENT_NEWS
        return Request.getRequest(url)
    }

    override suspend fun getNews(id: String): News {
        val url = "${routes.NEW_INFO}$id"
        return Request.getRequest(url)
    }
}