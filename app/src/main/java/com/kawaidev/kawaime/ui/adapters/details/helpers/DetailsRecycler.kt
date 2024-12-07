package com.kawaidev.kawaime.ui.adapters.details.helpers

import android.view.View
import android.widget.TextView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Screenshot
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.adapters.helpers.HorizontalRecycler
import com.kawaidev.kawaime.ui.adapters.helpers.HorizontalScreenRecycler
import com.kawaidev.kawaime.ui.adapters.helpers.HorizontalSeasonRecycler
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment

object DetailsRecycler {
    fun animeBind(itemView: View, fragment: DetailsFragment, items: List<BasicRelease>, title: String) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        titleTextView.text = title

        HorizontalRecycler.setup(fragment, items, itemView)
    }

    fun seasonBind(itemView: View, fragment: DetailsFragment, items: List<Season>, title: String) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        titleTextView.text = title

        HorizontalSeasonRecycler.setup(fragment, items, itemView)
    }

    fun screenBind(itemView: View, fragment: DetailsFragment, items: List<com.kawaidev.kawaime.network.dao.shikimori.Screenshot>, title: String) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        titleTextView.text = title

        HorizontalScreenRecycler.setup(fragment, items, itemView)
    }
}