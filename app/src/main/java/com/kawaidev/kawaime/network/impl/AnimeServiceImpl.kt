package com.kawaidev.kawaime.network.impl

import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.dao.anime.Schedule
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.network.routes.AnimeRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnimeServiceImpl(
    private val client: OkHttpClient
) : AnimeService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val routes = AnimeRoutes

    override suspend fun getAnime(id: String): Release = withContext(Dispatchers.IO) {
        val url = "${routes.info}$id"

        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject
                return@withContext json.decodeFromJsonElement<Release>(animeElement ?: jsonResponse)
            } else {
                println("Failed to fetch info: Empty response")
                return@withContext Release()
            }
        } else {
            println("Failed to fetch info: ${response.message}")
            return@withContext Release()
        }
    }

    override suspend fun searchAnime(searchParams: SearchParams): Pair<List<BasicRelease>, Boolean> = withContext(Dispatchers.IO) {
        val url = buildSearchUrl(searchParams)
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        println(url)

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject?.get("animes")
                val hasNextPage = jsonResponse.jsonObject["data"]
                    ?.jsonObject
                    ?.get("hasNextPage")
                    ?.jsonPrimitive
                    ?.booleanOrNull ?: false
                val release = json.decodeFromJsonElement<List<BasicRelease>>(animeElement ?: jsonResponse)
                return@withContext Pair(release, hasNextPage)
            } else {
                throw Exception("Failed to fetch info: Empty response")
            }
        } else {
            throw Exception("Failed to fetch info: ${response.message}")
        }
    }

    override suspend fun getHome(): Home = withContext(Dispatchers.IO) {
        val url = routes.spotlight
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject
                return@withContext json.decodeFromJsonElement<Home>(animeElement ?: jsonResponse)
            } else {
                println("Failed to fetch info: Empty response")
                return@withContext Home()
            }
        } else {
            println("Failed to fetch info: ${response.message}")
            return@withContext Home()
        }
    }

    override suspend fun getCategory(category: String, page: Int): Pair<List<BasicRelease>, Boolean> = withContext(Dispatchers.IO) {
        val url = "${routes.category}$category?page=$page"
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject?.get("animes")
                val hasNextPage = jsonResponse.jsonObject["data"]
                    ?.jsonObject
                    ?.get("hasNextPage")
                    ?.jsonPrimitive
                    ?.booleanOrNull ?: false
                val release = json.decodeFromJsonElement<List<BasicRelease>>(animeElement ?: jsonResponse)
                return@withContext Pair(release, hasNextPage)
            } else {
                throw Exception("Failed to fetch info: Empty response")
            }
        } else {
            throw Exception("Failed to fetch info: ${response.message}")
        }
    }

    override suspend fun getSchedule(date: LocalDate): List<Schedule> = withContext(Dispatchers.IO) {
        val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val url = "${routes.schedule}$formattedDate"
        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        println(url)

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val element = jsonResponse.jsonObject["data"]?.jsonObject?.get("scheduledAnimes")
                val schedule = json.decodeFromJsonElement<List<Schedule>>(element ?: jsonResponse)
                return@withContext schedule
            } else {
                throw Exception("Failed to fetch schedule: Empty response")
            }
        } else {
            throw Exception("Failed to fetch schedule: ${response.message}")
        }
    }

    private fun buildSearchUrl(searchParams: SearchParams): String {
        val urlBuilder = routes.search.toHttpUrlOrNull()?.newBuilder() ?: return ""

        urlBuilder.addQueryParameter("q", searchParams.q.takeIf { it.isNotBlank() } ?: "\"\"")
        if (searchParams.page != 1) urlBuilder.addQueryParameter("page", searchParams.page.toString())
        searchParams.type?.let { urlBuilder.addQueryParameter("type", it) }
        searchParams.status?.let { urlBuilder.addQueryParameter("status", it) }
        searchParams.rated?.let { urlBuilder.addQueryParameter("rated", it) }
        searchParams.score?.let { urlBuilder.addQueryParameter("score", it) }
        searchParams.season?.let { urlBuilder.addQueryParameter("season", it) }
        searchParams.language?.let { urlBuilder.addQueryParameter("language", it) }
        searchParams.start_date?.let { urlBuilder.addQueryParameter("start_date", it) }
        searchParams.end_date?.let { urlBuilder.addQueryParameter("end_date", it) }
        searchParams.sort?.let { urlBuilder.addQueryParameter("sort", it) }
        searchParams.genres?.let { urlBuilder.addQueryParameter("genres", it) }

        return urlBuilder.build().toString()
    }
}