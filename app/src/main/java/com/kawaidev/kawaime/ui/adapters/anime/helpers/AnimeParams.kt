package com.kawaidev.kawaime.ui.adapters.anime.helpers

import androidx.fragment.app.Fragment
import com.kawaidev.kawaime.network.dao.anime.BasicRelease

data class AnimeParams(
    val fragment: Fragment,
    var animeList: List<BasicRelease>,
    var nextPage: Boolean = false,
    var emptyMessage: String = "Opps, nothing is here!"
)
