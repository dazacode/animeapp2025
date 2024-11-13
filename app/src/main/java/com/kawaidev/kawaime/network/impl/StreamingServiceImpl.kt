package com.kawaidev.kawaime.network.impl

import android.util.Log
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.HlsStreamInfo
import com.kawaidev.kawaime.network.dao.streaming.Streaming
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.network.routes.StreamingRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
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
            println("Failed to fetch episodes: ${response.message}")
            return@withContext Episodes()
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
            println("Failed to fetch servers: ${response.message}")
            return@withContext EpisodeServers()
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
                println("Failed to fetch streaming: Empty response")
                return@withContext Streaming()
            }
        } else {
            println("Failed to fetch streaming: ${response.message}")
            return@withContext Streaming()
        }
    }

    override suspend fun getStreamingLink(url: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Make a request to fetch the HLS master playlist
                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = client.newCall(request).execute()
                val playlistContent = response.body?.string() ?: throw Exception("Failed to fetch HLS playlist")

                // Parse the HLS playlist and extract the streams information
                val streams = parseHlsMasterPlaylist(playlistContent)

                // Find the stream with the highest bandwidth
                val highestQualityStream = streams.maxByOrNull { it.bandwidth }

                // Log the highest quality stream URL for debugging
                // Make sure we have a valid highest quality stream
                val streamUrl = highestQualityStream?.url?.let { streamUrl ->
                    // Remove the "/master.hls" part from the original URL and replace it with the parsed stream URL
                    val baseUrl = url.substringBeforeLast("/master.m3u8")
                    return@let "$baseUrl/${streamUrl}"
                } ?: throw Exception("No valid streams found")

                Log.v("Service", streamUrl)

                streamUrl
            } catch (e: Exception) {
                throw Exception("Error fetching streaming link: ${e.message}")
            }
        }
    }

    private fun parseHlsMasterPlaylist(playlistContent: String): List<HlsStreamInfo> {
        val streamList = mutableListOf<HlsStreamInfo>()

        // Regex to match #EXT-X-STREAM-INF tags and extract the bandwidth and resolution
        val streamRegex = """#EXT-X-STREAM-INF:.*BANDWIDTH=(\d+),RESOLUTION=(\d+x\d+),.*\n([^\n]+)""".toRegex()

        val matches = streamRegex.findAll(playlistContent)

        for (match in matches) {
            val bandwidth = match.groupValues[1].toInt()
            val resolution = match.groupValues[2]
            val streamUrl = match.groupValues[3]

            // Add the stream info to the list
            streamList.add(HlsStreamInfo(streamUrl, bandwidth, resolution))
        }

        return streamList
    }
}