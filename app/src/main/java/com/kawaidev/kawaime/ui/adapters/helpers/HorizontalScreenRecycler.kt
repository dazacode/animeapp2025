package com.kawaidev.kawaime.ui.adapters.helpers

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.shikimori.Screenshot
import com.kawaidev.kawaime.ui.adapters.details.ScreenAdapter

object HorizontalScreenRecycler {
    fun setup(
        fragment: Fragment,
        data: List<Screenshot>,
        itemView: View,
        snap: Boolean = false
    ) {
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        val adapter = ScreenAdapter(fragment, data)

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