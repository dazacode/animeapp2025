package com.kawaidev.kawaime.ui.adapters.helpers

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.adapters.details.SeasonAdapter

object HorizontalSeasonRecycler {
    fun setup(
        fragment: Fragment,
        data: List<Season>,
        itemView: View,
        snap: Boolean = false
    ) {
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        val adapter = SeasonAdapter(fragment, data)

        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        recycler.adapter = adapter

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        if (snap && recycler.onFlingListener == null) {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recycler)
        }

        HorizontalRecycler.addDecoration(recycler)
    }
}