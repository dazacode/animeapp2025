package com.kawaidev.kawaime.ui.adapters.helpers

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.ui.adapters.explore.NewsAdapter
import com.kawaidev.kawaime.utils.Converts

object VerticalNewsRecycler {
    fun setup(
        fragment: Fragment,
        data: List<News>,
        itemView: View,
        snap: Boolean = false
    ) {
        val space = Converts.dpToPx(8f, fragment.requireContext()).toInt()
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        val adapter = NewsAdapter(data)

        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
        recycler.adapter = adapter

        recycler.isNestedScrollingEnabled = false

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        if (snap && recycler.onFlingListener == null) {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recycler)
        }

        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(
                SpacingItemDecoration(
                    Spacing(
                        vertical = space,
                    )
                )
            )
        }
    }
}