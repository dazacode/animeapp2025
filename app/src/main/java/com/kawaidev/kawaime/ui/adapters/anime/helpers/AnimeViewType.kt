package com.kawaidev.kawaime.ui.adapters.anime.helpers

import com.kawaidev.kawaime.network.dao.anime.BasicRelease

object AnimeViewType {
    const val VIEW_TYPE_ITEM = 0
    const val VIEW_TYPE_LOADING = 1
    const val VIEW_TYPE_ERROR = 2
    const val VIEW_TYPE_EMPTY = 3
    const val VIEW_TYPE_BOTTOM_LOADING = 4

    fun getItemCount(animeList: List<BasicRelease>, state: AnimeAdapterState, hasNextPage: Boolean): Int {
        return when (state) {
            AnimeAdapterState.ERROR, AnimeAdapterState.EMPTY -> 1
            AnimeAdapterState.LOADING -> 1
            AnimeAdapterState.DATA -> if (hasNextPage) animeList.size + 1 else animeList.size
        }
    }

    fun getItemViewType(position: Int, animeList: List<BasicRelease>, state: AnimeAdapterState, hasNextPage: Boolean): Int {
        return when (state) {
            AnimeAdapterState.ERROR -> VIEW_TYPE_ERROR
            AnimeAdapterState.LOADING -> VIEW_TYPE_LOADING
            AnimeAdapterState.EMPTY -> VIEW_TYPE_EMPTY
            AnimeAdapterState.DATA -> {
                if (position < animeList.size) VIEW_TYPE_ITEM
                else if (hasNextPage) VIEW_TYPE_BOTTOM_LOADING
                else VIEW_TYPE_ITEM
            }
        }
    }
}