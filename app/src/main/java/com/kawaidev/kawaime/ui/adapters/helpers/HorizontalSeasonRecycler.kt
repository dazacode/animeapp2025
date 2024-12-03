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
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.adapters.details.SeasonAdapter
import com.kawaidev.kawaime.utils.Converts

object HorizontalSeasonRecycler {
    fun setup(
        fragment: Fragment,
        data: List<Season>,
        itemView: View,
        snap: Boolean = false
    ) {
        val space = Converts.dpToPx(8f, fragment.requireContext()).toInt()
        val edgesSpace = Converts.dpToPx(Strings.PADDING, fragment.requireContext()).toInt()
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        val adapter = SeasonAdapter(fragment, data)

        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        recycler.adapter = adapter

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        if (snap && recycler.onFlingListener == null) {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recycler)
        }

        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(
                SpacingItemDecoration(
                    Spacing(
                        horizontal = space,
                        vertical = space,
                        edges = Rect(edgesSpace, edgesSpace, edgesSpace, edgesSpace)
                    )
                )
            )
        }
    }
}