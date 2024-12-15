package com.kawaidev.kawaime.ui.adapters.bottom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.bottomSheets.helpers.TitleItem

class TitlesAdapter(
    private val titles: List<TitleItem>,
    private val onCopyClicked: (String) -> Unit
) : RecyclerView.Adapter<TitlesAdapter.TitleViewHolder>() {

    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val content: TextView = view.findViewById(R.id.content)
        val copyButton: ImageButton = view.findViewById(R.id.copyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_title, parent, false)
        return TitleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        val titleItem = titles[position]
        holder.label.text = titleItem.label
        holder.content.text = titleItem.content ?: "Not available"
        holder.content.visibility = if (titleItem.content.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.copyButton.visibility = if (titleItem.copyEnabled) View.VISIBLE else View.GONE

        holder.copyButton.setOnClickListener {
            titleItem.content?.let { onCopyClicked(it) }
        }
    }

    override fun getItemCount(): Int = titles.size
}