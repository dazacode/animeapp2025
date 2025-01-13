package com.kawaidev.kawaime.ui.adapters.details.helpers

import android.view.View
import android.widget.TextView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.anime.helpers.TitleHeader
import com.kawaidev.kawaime.ui.adapters.details.ScreenAdapter
import com.kawaidev.kawaime.ui.adapters.details.SeasonAdapter
import com.kawaidev.kawaime.ui.adapters.helpers.HorizontalRecycler
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment

object DetailsRecycler {
    fun animeBind(itemView: View, fragment: DetailsFragment, items: List<BasicRelease>, title: String) {
        HorizontalRecycler.setup(
            itemView,
            AnimeAdapter(AnimeParams(fragment, items)),
            horizontalPadding = Strings.DETAILS_PADDING,
            headerData = TitleHeader(title)
        )
    }

    fun seasonBind(itemView: View, fragment: DetailsFragment, items: List<Season>, title: String) {
        HorizontalRecycler.setup(
            itemView,
            SeasonAdapter(fragment, items),
            horizontalPadding = Strings.DETAILS_PADDING,
            headerData = TitleHeader(title)
        )
    }

    fun screenBind(itemView: View, fragment: DetailsFragment, items: List<com.kawaidev.kawaime.network.dao.shikimori.Screenshot>, title: String) {
        HorizontalRecycler.setup(
            itemView,
            ScreenAdapter(fragment, items),
            horizontalPadding = Strings.DETAILS_PADDING,
            headerData = TitleHeader(title)
        )
    }
}