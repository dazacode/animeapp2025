package com.kawaidev.kawaime

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.kawaidev.kawaime.network.dao.helpers.WatchedRelease
import com.kawaidev.kawaime.ui.listeners.FavoriteListener
import com.kawaidev.kawaime.ui.listeners.SearchListener
import com.kawaidev.kawaime.ui.listeners.WatchedListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

class Prefs private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    private val searchListeners = mutableListOf<WeakReference<SearchListener>>()
    private val watchedListeners = mutableListOf<WeakReference<WatchedListener>>()
    private val favoriteListeners = mutableListOf<WeakReference<FavoriteListener>>()

    companion object {
        @Volatile
        private var INSTANCE: Prefs? = null

        fun getInstance(context: Context): Prefs {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Prefs(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun setSearchListener(listener: SearchListener) {
        searchListeners.add(WeakReference(listener))
    }

    fun setWatchedListener(listener: WatchedListener) {
        watchedListeners.add(WeakReference(listener))
    }

    fun setFavoriteListener(listener: FavoriteListener) {
        favoriteListeners.add(WeakReference(listener))
    }

    fun addSearchTerm(searchTerm: String) {
        if (searchTerm.isNotBlank()) {
            val currentSearches = getSearches().toMutableList()
            currentSearches.remove(searchTerm)
            currentSearches.add(0, searchTerm)
            saveSearches(currentSearches)
            searchListeners.forEach { it.get()?.onSearchAdded(getSearches()) }
        }
    }

    fun getRecentSearches(): List<String> {
        return getSearches()
    }

    fun clearSearches() {
        saveSearches(mutableListOf())

        searchListeners.forEach { it.get()?.onSearchAdded(getSearches()) }
    }

    private fun saveSearches(searches: List<String>) {
        val jsonStr = json.encodeToString(searches)
        sharedPreferences.edit().putString("searches", jsonStr).apply()
    }

    fun getSearches(): List<String> {
        val jsonStr = sharedPreferences.getString("searches", null) ?: return mutableListOf()
        return json.decodeFromString<List<String>>(jsonStr)
    }

    // New Methods for WatchedRelease

    fun saveWatchedRelease(watchedRelease: WatchedRelease) {
        // Get all saved watched releases and filter out any with the same episode ID
        val watchedReleases = getAllWatched().toMutableList().apply {
            removeAll { it.episodeId == watchedRelease.episodeId }
        }

        // Add the new watched release
        watchedReleases.add(watchedRelease)

        Log.d("Prefs", json.encodeToString(watchedRelease))

        // Save the updated list back to SharedPreferences
        val jsonStr = json.encodeToString(watchedReleases)
        sharedPreferences.edit().putString("watched_releases", jsonStr).apply()

        watchedListeners.forEach { it.get()?.onChange() }
    }

    fun getAllWatched(): List<WatchedRelease> {
        val jsonStr = sharedPreferences.getString("watched_releases", null) ?: return emptyList()
        return json.decodeFromString<List<WatchedRelease>>(jsonStr)
    }

    fun findByEpisodeId(episodeId: String): WatchedRelease? {
        println(getAllWatched().find { it.episodeId == episodeId })
        return getAllWatched().find { it.episodeId == episodeId }
    }

    fun clearWatchedReleases() {
        sharedPreferences.edit().remove("watched_releases").apply()
    }

    fun addFavorite(id: String) {
        val favorites = getFavorites().toMutableList()

        favorites.remove(id) // Remove if it already exists
        favorites.add(0, id) // Add to the top

        saveFavorites(favorites)
        favoriteListeners.forEach { it.get()?.onChange() }
    }

    fun removeFavorite(id: String) {
        val favorites = getFavorites().toMutableList()
        if (favorites.remove(id)) { // Remove only if it exists
            saveFavorites(favorites)
            favoriteListeners.forEach { it.get()?.onChange() }
        }
    }

    fun isFavorite(id: String): Boolean {
        return getFavorites().contains(id)
    }

    // Fetch favorites as a list
    fun getFavorites(): List<String> {
        val jsonStr = sharedPreferences.getString("favorites", null) ?: return emptyList()
        return json.decodeFromString(jsonStr)
    }

    // Save favorites as a JSON string
    private fun saveFavorites(favorites: List<String>) {
        val jsonStr = json.encodeToString(favorites)
        sharedPreferences.edit().putString("favorites", jsonStr).apply()
    }

}