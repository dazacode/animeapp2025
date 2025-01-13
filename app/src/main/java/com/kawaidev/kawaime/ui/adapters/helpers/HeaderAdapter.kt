package com.kawaidev.kawaime.ui.adapters.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.ui.adapters.anime.helpers.TitleHeader
import com.kawaidev.kawaime.utils.Converts

class HeaderAdapter(
    private val headerData: Any,
    private val horizontalPadding: Float
) : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.title_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        if (headerData is TitleHeader) holder.bind(headerData)
    }

    override fun getItemCount(): Int = 1

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(headerData: TitleHeader) {
            val headerTextView: TextView = itemView.findViewById(R.id.title)
            headerTextView.text = headerData.title
            
            val padding = Converts.dpToPx(Strings.PADDING, itemView.context).toInt()
            val horizontalPadding = Converts.dpToPx(horizontalPadding, itemView.context).toInt()

            headerTextView.setPadding(horizontalPadding, padding, horizontalPadding, padding)
        }
    }
}