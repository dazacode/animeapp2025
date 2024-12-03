package com.kawaidev.kawaime.ui.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.kawaidev.kawaime.network.dao.anime.BasicRelease

class AnimeDiffCallback(
    private val oldList: List<BasicRelease>,
    private val newList: List<BasicRelease>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}