package com.kawaidev.kawaime

import android.content.Context
import android.content.SharedPreferences
import com.kawaidev.kawaime.ui.listeners.SearchListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

class Prefs private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    private val searchListeners = mutableListOf<WeakReference<SearchListener>>()

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
}