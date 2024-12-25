package com.kawaidev.kawaime.ui.adapters.helpers

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.decoration.LinearDecoration
import com.kawaidev.kawaime.utils.Converts

object HorizontalRecycler {
    fun setup(
        fragment: Fragment,
        data: List<BasicRelease>,
        itemView: View,
        snap: Boolean = false
    ) {
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        val adapter = AnimeAdapter(AnimeParams(fragment, data))

        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        recycler.adapter = adapter

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        if (snap && recycler.onFlingListener == null) {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recycler)
        }

        addDecoration(recycler)
    }

    fun addDecoration(recycler: RecyclerView) {
        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(LinearDecoration(
                horizontal = Converts.dpToPx(Strings.PADDING_BETWEEN, recycler.context).toInt(),
                vertical = Converts.dpToPx(Strings.PADDING_BETWEEN, recycler.context).toInt(),
                edges = Converts.dpToPx(Strings.PADDING, recycler.context).toInt(),
            ))
        }
    }
}