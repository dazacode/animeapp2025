package com.kawaidev.kawaime.ui.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.ui.adapters.details.helpers.DetailsViewType

class ReleaseDiffCallback(
    private val oldRelease: Release,
    private val newRelease: Release
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return DetailsViewType.getItemCount(false, false, oldRelease)
    }

    override fun getNewListSize(): Int {
        return DetailsViewType.getItemCount(false, false, newRelease)
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return DetailsViewType.getItemTypeAtPosition(oldItemPosition, oldRelease) ==
                DetailsViewType.getItemTypeAtPosition(newItemPosition, newRelease)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when (DetailsViewType.getItemTypeAtPosition(oldItemPosition, oldRelease)) {
            DetailsViewType.VIEW_TYPE_HEADER -> oldRelease.anime == newRelease.anime
            DetailsViewType.VIEW_TYPE_SEASONS -> oldRelease.seasons == newRelease.seasons
            DetailsViewType.VIEW_TYPE_RELATED -> oldRelease.relatedAnimes == newRelease.relatedAnimes
            DetailsViewType.VIEW_TYPE_RECOMMENDATIONS -> oldRelease.recommendedAnimes == newRelease.recommendedAnimes
            else -> true
        }
    }
}