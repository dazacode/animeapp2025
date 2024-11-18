package com.kawaidev.kawaime.ui.adapters.anime.helpers

import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter

object AnimeHelper {
    fun updateData(adapter: AnimeAdapter, anime: List<SearchResponse>, recycler: RecyclerView) {
        adapter.setLoading(false)
        adapter.updateData(anime)
        adapter.setEmpty(anime.isEmpty())

        recycler.post {
            recycler.invalidateItemDecorations()
        }
    }
}