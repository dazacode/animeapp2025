package com.kawaidev.kawaime.ui.adapters.anime.helpers

import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter

object AnimeHelper {
    fun updateData(adapter: AnimeAdapter, anime: List<BasicRelease>, recycler: RecyclerView) {
        adapter.updateData(anime)

        if (recycler.itemDecorationCount > 0) {
            recycler.post {
                recycler.invalidateItemDecorations()
                recycler.requestLayout()
            }
        }
    }
}