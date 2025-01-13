package com.kawaidev.kawaime.ui.adapters.helpers

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.ui.adapters.decoration.LinearDecoration
import com.kawaidev.kawaime.utils.Converts

object HorizontalRecycler {
    fun setup(
        itemView: View,
        adapter: RecyclerView.Adapter<*>,
        snap: Boolean = false,
        horizontalPadding: Float = Strings.PADDING_BETWEEN,
        headerData: Any? = null
    ) {
        val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)

        val concatAdapter = if (headerData != null) {
            val headerAdapter = HeaderAdapter(headerData, horizontalPadding)
            val horizontalRecyclerAdapter = HorizontalRecyclerAdapter(adapter, snap, horizontalPadding)
            ConcatAdapter(headerAdapter, horizontalRecyclerAdapter)
        } else {
            adapter
        }

        recycler.adapter = concatAdapter
    }

    fun setupSnap(recycler: RecyclerView, snap: Boolean) {
        if (snap && recycler.onFlingListener == null) {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recycler)
        }
    }

    fun addDecoration(recycler: RecyclerView, horizontalPadding: Int) {
        val padding = Converts.dpToPx(Strings.PADDING, recycler.context).toInt()

        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(LinearDecoration(
                horizontal = Converts.dpToPx(Strings.PADDING_BETWEEN, recycler.context).toInt(),
                vertical = Converts.dpToPx(Strings.PADDING_BETWEEN, recycler.context).toInt(),
                edges = Rect(horizontalPadding, padding, horizontalPadding, padding)
            ))
        }
    }
}