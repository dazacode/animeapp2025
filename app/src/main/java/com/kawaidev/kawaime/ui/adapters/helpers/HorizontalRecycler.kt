package com.kawaidev.kawaime.ui.adapters.helpers

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.utils.Converts

object HorizontalRecycler {
    fun setup(fragment: Fragment, data: List<SearchResponse>, itemView: View, snap: Boolean = true) {
        val space = Converts.dpToPx(8f, fragment.requireContext()).toInt()

        val latestAdapter = AnimeAdapter(AnimeParams(fragment, data))
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        recycler.adapter = latestAdapter

        if (snap) {
            val snapHelper = LinearSnapHelper()

            if (recycler.onFlingListener == null) {
                snapHelper.attachToRecyclerView(recycler)
            }
        }

        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(
                SpacingItemDecoration(
                    Spacing(
                        horizontal = space,
                        vertical = space,
                        edges = Rect(space, space, space, space)
                    )
                )
            )
        }
    }
}