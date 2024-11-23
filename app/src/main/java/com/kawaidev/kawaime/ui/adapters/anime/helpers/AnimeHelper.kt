package com.kawaidev.kawaime.ui.adapters.anime.helpers

import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter

object AnimeHelper {
    fun updateData(adapter: AnimeAdapter, anime: List<SearchResponse>, recycler: RecyclerView) {
        adapter.setEmpty(anime.isEmpty())
        adapter.setLoading(false)
        adapter.updateData(anime)

        if (recycler.itemDecorationCount > 0) {
            recycler.post {
                recycler.invalidateItemDecorations()
                recycler.requestLayout()
            }
        }
    }
}