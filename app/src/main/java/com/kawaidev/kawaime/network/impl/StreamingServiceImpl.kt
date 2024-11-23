package com.kawaidev.kawaime.network.impl

import com.kawaidev.kawaime.network.dao.api_utils.ApiException
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.network.routes.StreamingRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class StreamingServiceImpl(
    private val client: OkHttpClient
) : StreamingService {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val routes = StreamingRoutes

    override suspend fun getEpisodes(id: String): Episodes = withContext(Dispatchers.IO) {
        val url = "${routes.episodes}$id/episodes"
        println(url)

        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject
                return@withContext json.decodeFromJsonElement<Episodes>(animeElement ?: jsonResponse)
            } else {
                println("Failed to fetch episodes: Empty response")
                return@withContext Episodes()
            }
        } else {
            val errorBody = response.body?.string()
            val errorMessage = try {
                errorBody?.let {
                    json.parseToJsonElement(it).jsonObject["message"]?.jsonPrimitive?.content
                }
            } catch (e: Exception) {
                null
            } ?: response.message

            val status = try {
                errorBody?.let {
                    json.parseToJsonElement(it).jsonObject["status"]?.jsonPrimitive?.int
                }
            } catch (e: Exception) {
                null
            } ?: response.code

            throw ApiException(status, errorMessage)
        }
    }

    override suspend fun getServers(id: String): EpisodeServers = withContext(Dispatchers.IO) {
        val url = "${routes.servers}$id"

        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject
                return@withContext json.decodeFromJsonElement<EpisodeServers>(animeElement ?: jsonResponse)
            } else {
                println("Failed to fetch servers: Empty response")
                return@withContext EpisodeServers()
            }
        } else {
            val errorBody = response.body?.string()
            val errorMessage = try {
                errorBody?.let {
                    json.parseToJsonElement(it).jsonObject["message"]?.jsonPrimitive?.content
                }
            } catch (e: Exception) {
                null
            } ?: response.message

            val status = try {
                errorBody?.let {
                    json.parseToJsonElement(it).jsonObject["status"]?.jsonPrimitive?.int
                }
            } catch (e: Exception) {
                null
            } ?: response.code

            throw ApiException(status, errorMessage)
        }
    }

    override suspend fun getStreaming(params: StreamingParams): Streaming = withContext(Dispatchers.IO) {
        val url = "${routes.streaming}?${params.toQueryString()}"

        val request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody: String? = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = json.parseToJsonElement(responseBody)
                val animeElement = jsonResponse.jsonObject["data"]?.jsonObject
                return@withContext json.decodeFromJsonElement<Streaming>(animeElement ?: jsonResponse)
            } else {
                throw Exception("Failed to fetch streaming: Empty response")
            }
        } else {
            val errorBody = response.body?.string()
            val errorMessage = try {
                errorBody?.let {
                    json.parseToJsonElement(it).jsonObject["message"]?.jsonPrimitive?.content
                }
            } catch (e: Exception) {
                null
            } ?: response.message

            val status = try {
                errorBody?.let {
                    json.parseToJsonElement(it).jsonObject["status"]?.jsonPrimitive?.int
                }
            } catch (e: Exception) {
                null
            } ?: response.code

            throw ApiException(status, errorMessage)
        }
    }
}