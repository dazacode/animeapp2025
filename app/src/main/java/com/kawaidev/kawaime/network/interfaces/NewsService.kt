package com.kawaidev.kawaime.network.interfaces

import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.network.impl.NewsServiceImpl

interface NewsService {
    suspend fun getRecentNews(): List<News>
    suspend fun getNews(id: String): News

    companion object {
        fun create(): NewsService {
            return NewsServiceImpl()
        }
    }
}