package com.kawaidev.kawaime.ui.adapters.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.decoration.LinearDecoration
import com.kawaidev.kawaime.utils.Converts

class HorizontalRecyclerAdapter(
    private val adapter: RecyclerView.Adapter<*>,
    private val snap: Boolean,
    private val horizontalPadding: Float
) : RecyclerView.Adapter<HorizontalRecyclerAdapter.HorizontalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1

    inner class HorizontalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler)
            recyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recyclerView.adapter = adapter

            HorizontalRecycler.setupSnap(recyclerView, snap)
            HorizontalRecycler.addDecoration(recyclerView, Converts.dpToPx(horizontalPadding, itemView.context).toInt())
        }
    }
}