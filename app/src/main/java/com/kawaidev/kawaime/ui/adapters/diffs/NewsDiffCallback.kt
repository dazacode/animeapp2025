package com.kawaidev.kawaime.ui.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.kawaidev.kawaime.network.dao.news.News

class NewsDiffCallback(
    private val oldList: List<News?>,
    private val newList: List<News?>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        // Check for nulls before comparing IDs
        return oldItem?.id == newItem?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        // Ensure content comparison handles nulls safely
        return oldItem == newItem
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size
}