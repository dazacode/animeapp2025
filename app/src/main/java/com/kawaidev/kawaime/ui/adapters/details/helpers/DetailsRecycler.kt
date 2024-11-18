package com.kawaidev.kawaime.ui.adapters.details.helpers

import android.graphics.Rect
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.adapters.details.SeasonAdapter
import com.kawaidev.kawaime.ui.adapters.helpers.HorizontalRecycler
import com.kawaidev.kawaime.ui.adapters.helpers.HorizontalSeasonRecycler
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.Converts

object DetailsRecycler {
    fun animeBind(itemView: View, fragment: DetailsFragment, items: List<SearchResponse>, title: String) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        titleTextView.text = title

        HorizontalRecycler.setup(fragment, items, itemView)
    }

    fun seasonBind(itemView: View, fragment: DetailsFragment, items: List<Season>, title: String) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        titleTextView.text = title

        HorizontalSeasonRecycler.setup(fragment, items, itemView, snap = false)
    }
}