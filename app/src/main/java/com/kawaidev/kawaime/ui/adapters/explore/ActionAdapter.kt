package com.kawaidev.kawaime.ui.adapters.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.explore.helpers.ActionItem

class ActionAdapter(
    private val actions: List<ActionItem>
) : RecyclerView.Adapter<ActionAdapter.ActionViewHolder>() {

    inner class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layout: LinearLayout = itemView.findViewById(R.id.actionButtonClickable)
        private val icon: ImageView = itemView.findViewById(R.id.actionIcon)
        private val label: TextView = itemView.findViewById(R.id.actionLabel)

        fun bind(action: ActionItem) {
            layout.setBackgroundResource(action.backgroundColorResId)
            icon.setImageResource(action.iconResId)
            icon.imageTintList = ContextCompat.getColorStateList(itemView.context, action.tintColorResId)
            label.setTextColor(ContextCompat.getColor(itemView.context, action.tintColorResId))
            label.text = action.label
            itemView.setOnClickListener { action.onClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_action_adapter, parent, false)
        return ActionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int = actions.size
}
