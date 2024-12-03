package com.kawaidev.kawaime.ui.adapters.anime.helpers

import com.kawaidev.kawaime.network.dao.anime.BasicRelease

object AnimeViewType {
    const val VIEW_TYPE_ITEM = 0
    const val VIEW_TYPE_LOADING = 1
    const val VIEW_TYPE_ERROR = 2
    const val VIEW_TYPE_EMPTY = 3
    const val VIEW_TYPE_BOTTOM_LOADING = 4

    fun getItemCount(animeList: List<BasicRelease>, state: AnimeAdapterState): Int {
        return when (state) {
            AnimeAdapterState.LOADING -> 1
            AnimeAdapterState.ERROR -> 1
            AnimeAdapterState.EMPTY -> 1
            AnimeAdapterState.BOTTOM_LOADING -> animeList.size + 1
            AnimeAdapterState.DATA -> animeList.size
        }
    }

    fun getItemViewType(position: Int, animeList: List<BasicRelease>, state: AnimeAdapterState): Int {
        return when (state) {
            AnimeAdapterState.ERROR -> VIEW_TYPE_ERROR
            AnimeAdapterState.LOADING -> VIEW_TYPE_LOADING
            AnimeAdapterState.EMPTY -> VIEW_TYPE_EMPTY
            AnimeAdapterState.BOTTOM_LOADING -> {
                if (position < animeList.size) VIEW_TYPE_ITEM
                else VIEW_TYPE_BOTTOM_LOADING
            }
            AnimeAdapterState.DATA -> VIEW_TYPE_ITEM
        }
    }
}