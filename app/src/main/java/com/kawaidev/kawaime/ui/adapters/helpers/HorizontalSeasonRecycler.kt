package com.kawaidev.kawaime.ui.adapters.helpers

import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.details.SeasonAdapter
import com.kawaidev.kawaime.utils.Converts

object HorizontalSeasonRecycler {
    private val recyclerViewStates = mutableMapOf<String, Parcelable?>()

    fun setup(
        fragment: Fragment,
        data: List<Season>,
        itemView: View,
        snap: Boolean = true
    ) {
        val space = Converts.dpToPx(8f, fragment.requireContext()).toInt()
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        val latestAdapter = SeasonAdapter(fragment, data)

        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        recycler.adapter = latestAdapter

        val key = "${fragment.javaClass.name}_${data.hashCode()}_${itemView.id}"
        recycler.layoutManager?.onRestoreInstanceState(recyclerViewStates[key])

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerViewStates[key] = recyclerView.layoutManager?.onSaveInstanceState()
                }
            }
        })

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