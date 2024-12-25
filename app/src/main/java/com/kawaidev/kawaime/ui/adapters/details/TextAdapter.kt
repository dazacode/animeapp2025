package com.kawaidev.kawaime.ui.adapters.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.details.helpers.TextItem

class TextAdapter(
    private val items: List<TextItem>
) : RecyclerView.Adapter<TextAdapter.TextViewHolder>() {

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconView: ImageView = itemView.findViewById(R.id.icon)
        private val textView: TextView = itemView.findViewById(R.id.text)

        fun bind(item: TextItem) {
            if (item.iconResId != null) iconView.setImageResource(item.iconResId) else
                iconView.visibility = View.GONE

            val layoutParams = iconView.layoutParams
            layoutParams.width = item.iconSize
            layoutParams.height = item.iconSize
            iconView.layoutParams = layoutParams

            textView.text = item.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text_item, parent, false)
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}