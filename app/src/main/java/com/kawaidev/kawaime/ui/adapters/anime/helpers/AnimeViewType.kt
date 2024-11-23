package com.kawaidev.kawaime.ui.adapters.anime.helpers

import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter

object AnimeViewType {
    const val VIEW_TYPE_ITEM = 0
    const val VIEW_TYPE_LOADING = 1
    const val VIEW_TYPE_ERROR = 2
    const val VIEW_TYPE_EMPTY = 3
    const val VIEW_TYPE_BOTTOM_LOADING = 4

    fun getItemCount(animeList: List<SearchResponse>, isLoading: Boolean, isError: Boolean, isEmpty: Boolean, nextPage: Boolean): Int {
        return when {
            animeList.isEmpty() && isLoading -> 1
            isError -> 1
            isEmpty && !isError -> 1
            animeList.isNotEmpty() && isLoading && nextPage -> animeList.size + 1
            else -> animeList.size
        }
    }

    fun getItemViewType(position: Int, animeList: List<SearchResponse>, isLoading: Boolean, isError: Boolean, isEmpty: Boolean, nextPage: Boolean): Int {
        return when {
            isError -> VIEW_TYPE_ERROR
            isLoading && animeList.isEmpty() -> VIEW_TYPE_LOADING
            isEmpty && !isError -> VIEW_TYPE_EMPTY
            position == animeList.size && isLoading && nextPage -> VIEW_TYPE_BOTTOM_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }
}