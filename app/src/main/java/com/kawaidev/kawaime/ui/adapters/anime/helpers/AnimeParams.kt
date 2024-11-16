package com.kawaidev.kawaime.ui.adapters.anime.helpers

import androidx.fragment.app.Fragment
import com.kawaidev.kawaime.network.dao.anime.SearchResponse

data class AnimeParams(
    val fragment: Fragment,
    var animeList: List<SearchResponse>,
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var isEmpty: Boolean = false,
    var nextPage: Boolean = false,
    var emptyMessage: String = "Opps, nothing is here!"
)
