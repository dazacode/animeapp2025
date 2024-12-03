package com.kawaidev.kawaime.network.impl

import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.network.interfaces.NewsService
import com.kawaidev.kawaime.network.routes.NewsRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class NewsServiceImpl (
    private val client: OkHttpClient
) : NewsService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val routes = NewsRoutes

    override suspend fun getRecentNews(): List<News> = withContext(Dispatchers.IO) {
        val url = routes.RECENT_NEWS

        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                return@withContext json.decodeFromJsonElement<List<News>>(jsonResponse)
            } else {
                println("Failed to fetch news: Empty response")
                return@withContext emptyList()
            }
        } else {
            println("Failed to fetch news: ${response.message}")
            return@withContext emptyList()
        }
    }

    override suspend fun getNews(id: String): News = withContext(Dispatchers.IO) {
        val url = "${routes.NEW_INFO}$id"

        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                return@withContext json.decodeFromJsonElement<News>(jsonResponse)
            } else {
                println("Failed to fetch news: Empty response")
                return@withContext News()
            }
        } else {
            println("Failed to fetch news: ${response.message}")
            return@withContext News()
        }
    }
}