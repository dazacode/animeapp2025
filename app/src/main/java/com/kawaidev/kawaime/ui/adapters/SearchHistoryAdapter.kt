package com.kawaidev.kawaime.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment

class SearchHistoryAdapter(
    private val fragment: SearchFragment,
    private var history: List<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_HISTORY = 1
    }

    // ViewHolder for history items
    class SearchHistoryAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val search: TextView = itemView.findViewById(R.id.search)
    }

    // ViewHolder for header
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText: TextView = itemView.findViewById(R.id.headerTitle)
        val headerButton: TextView = itemView.findViewById(R.id.headerButton)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_HISTORY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_search_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_search_item, parent, false)
            SearchHistoryAdapterHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_HEADER) {
            val headerHolder = holder as HeaderViewHolder
            headerHolder.headerButton.setOnClickListener {
                fragment.clearSearches()
            }
        } else {
            val searchHolder = holder as SearchHistoryAdapterHolder
            val search = history[position - 1] // Adjust for header at position 0

            searchHolder.search.text = search

            searchHolder.itemView.setOnClickListener {
                fragment.pasteText(search)
            }
        }
    }

    override fun getItemCount(): Int {
        return history.size + 1 // +1 for the header
    }

    fun updateData(oldHistory: List<String>) {
        history = oldHistory
        notifyDataSetChanged()
    }
}