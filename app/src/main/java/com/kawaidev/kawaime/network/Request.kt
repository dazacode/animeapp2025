package com.kawaidev.kawaime.network

import com.kawaidev.kawaime.App
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Request {
    val client: OkHttpClient = App.httpClient

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Sends an HTTP GET or POST request and handles the response via the provided callback interface.
     *
     * @param url The URL to request.
     * @param method The HTTP method, either "GET" or "POST".
     * @param body Optional request body for POST requests.
     * @param jsonPath Optional JSON path to extract specific data from the response.
     * @param callback The callback interface to handle request lifecycle events.
     */
    @PublishedApi
    internal inline fun <reified T> sendRequest(
        url: String,
        method: String = "GET",
        body: String? = null,
        jsonPath: String? = null,
        callback: com.kawaidev.kawaime.network.callbacks.Request<T>
    ) {
        callback.onStarted()

        val requestBuilder = Request.Builder().url(url)
        if (method.uppercase() == "POST" && body != null) {
            val requestBody = body.toRequestBody("application/json".toMediaType())
            requestBuilder.post(requestBody)
        }

        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Request failed with status: ${response.code}"))
                    return
                }

                val responseBody = response.body?.string()
                if (responseBody == null) {
                    callback.onError(IOException("Response body is null"))
                    return
                }

                try {
                    val jsonElement = json.parseToJsonElement(responseBody)
                    val extractedJson = jsonPath?.let { extractFromPath(jsonElement, it) } ?: jsonElement
                    val serializer = serializer<T>() // Use serializer() for reified type
                    val parsedData: T = json.decodeFromJsonElement(serializer, extractedJson)
                    callback.onSuccess(parsedData)
                } catch (e: Exception) {
                    callback.onError(IOException("Failed to parse response: ${e.message}"))
                }
            }
        })
    }

    /**
     * Extracts JSON data based on the given JSON path.
     *
     * @param jsonElement The root JSON element.
     * @param jsonPath The JSON path to extract (e.g., "data.attributes").
     * @return The extracted JSON element.
     */
    fun extractFromPath(jsonElement: JsonElement, jsonPath: String): JsonElement {
        var currentElement: JsonElement = jsonElement
        val pathParts = jsonPath.split(".")
        for (part in pathParts) {
            if (currentElement is JsonObject && currentElement.containsKey(part)) {
                currentElement = currentElement[part]!!
            } else {
                throw IllegalArgumentException("Invalid JSON path: $jsonPath")
            }
        }
        return currentElement
    }

    suspend inline fun <reified T> getRequest(url: String, jsonPath: String? = null): T {
        return suspendCoroutine { continuation ->
            sendRequest<T>(
                url = url, jsonPath = jsonPath,
                callback = object : com.kawaidev.kawaime.network.callbacks.Request<T> {
                    override fun onSuccess(data: T) {
                        super.onSuccess(data)
                        continuation.resume(data)
                    }

                    override fun onError(error: Exception) {
                        super.onError(error)
                        continuation.resumeWithException(error)
                    }
                },
            )
        }
    }
}