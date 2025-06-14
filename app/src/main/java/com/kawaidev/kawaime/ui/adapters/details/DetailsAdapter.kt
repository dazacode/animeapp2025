package com.kawaidev.kawaime.ui.adapters.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.Screenshot
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.details.helpers.DetailsHelper
import com.kawaidev.kawaime.ui.adapters.details.helpers.DetailsRecycler
import com.kawaidev.kawaime.ui.adapters.details.helpers.DetailsViewType
import com.kawaidev.kawaime.ui.adapters.diffs.ReleaseDiffCallback
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment

class DetailsAdapter(
    private val fragment: DetailsFragment,
    private var release: Release,
    private var isLoading: Boolean = false,
    private var isExpanded: Boolean = false,
    private var isError: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return DetailsViewType.getItemViewType(isLoading, isError, position, release)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DetailsViewType.VIEW_TYPE_LOADING -> LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.loading_view, parent, false))
            DetailsViewType.VIEW_TYPE_ERROR -> ErrorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.error_view, parent, false))
            DetailsViewType.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
            DetailsViewType.VIEW_TYPE_DESCRIPTION -> DescriptionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_description, parent, false))
            DetailsViewType.VIEW_TYPE_SCREENSHOTS -> ScreenshotsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            DetailsViewType.VIEW_TYPE_SEASONS -> SeasonsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            DetailsViewType.VIEW_TYPE_RELATED -> RelatedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            DetailsViewType.VIEW_TYPE_RECOMMENDATIONS -> RecommendationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DetailsViewType.VIEW_TYPE_LOADING -> (holder as LoadingViewHolder).bind()
            DetailsViewType.VIEW_TYPE_ERROR -> (holder as ErrorViewHolder).bind()
            DetailsViewType.VIEW_TYPE_HEADER -> (holder as HeaderViewHolder).bind(release)
            DetailsViewType.VIEW_TYPE_DESCRIPTION -> (holder as DescriptionViewHolder).bind(release.anime?.info?.description ?: "")
            DetailsViewType.VIEW_TYPE_SCREENSHOTS -> release.shikimori?.screenshots?.let { (holder as ScreenshotsViewHolder).bind(it) }
            DetailsViewType.VIEW_TYPE_SEASONS -> release.seasons?.let { (holder as SeasonsViewHolder).bind(it) }
            DetailsViewType.VIEW_TYPE_RELATED -> release.relatedAnimes?.let { (holder as RelatedViewHolder).bind(it) }
            DetailsViewType.VIEW_TYPE_RECOMMENDATIONS -> release.recommendedAnimes?.let {
                (holder as RecommendationsViewHolder).bind(
                    it
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return DetailsViewType.getItemCount(isLoading, isError, release)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {

        }
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val helpButton: Button = view.findViewById(R.id.help_button)
        private val tryAgainButton: Button = view.findViewById(R.id.again_button)

        fun bind() {
            helpButton.setOnClickListener {
                (fragment.requireActivity() as MainActivity).dialogs.onHelp()
            }

            tryAgainButton.setOnClickListener {
                fragment.viewModel.fetchAnime(fragment.id)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(release: Release) {
            if (itemView.tag != release) {
                itemView.tag = release
                DetailsHelper.headerBind(fragment, itemView, release)
            } else {
                val isFavorite = fragment.prefs.isFavorite(release.anime?.info?.id ?: "")
                DetailsHelper.updateFavoriteIcon(isFavorite, itemView.findViewById(R.id.favoriteButton), fragment)
            }
        }
    }

    inner class DescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(description: String) {
            DetailsHelper.descBind(description, itemView, isExpanded) {
                isExpanded = it
            }
        }
    }

    inner class ScreenshotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(screenshots: List<com.kawaidev.kawaime.network.dao.shikimori.Screenshot>) {
            DetailsRecycler.screenBind(itemView, fragment, screenshots, itemView.context.getString(R.string.screenshots))
        }
    }

    inner class SeasonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(seasonItems: List<Season>) {
            DetailsRecycler.seasonBind(itemView, fragment, seasonItems, itemView.context.getString(R.string.seasons))
        }
    }

    inner class RelatedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(relatedItems: List<BasicRelease>) {
            DetailsRecycler.animeBind(itemView, fragment, relatedItems, itemView.context.getString(R.string.related))
        }
    }

    inner class RecommendationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recommendationItems: List<BasicRelease>) {
            DetailsRecycler.animeBind(itemView, fragment, recommendationItems, itemView.context.getString(R.string.recommendations))
        }
    }

    fun setData(newRelease: Release) {
        val diffCallback = ReleaseDiffCallback(release, newRelease)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        release = newRelease
        diffResult.dispatchUpdatesTo(this)
    }

    fun setLoading(loading: Boolean) {
        if (isLoading != loading) {
            isLoading = loading
            notifyDataSetChanged()
        }
    }

    fun setError(error: Boolean) {
        if (isError != error) {
            isError = error
            notifyDataSetChanged()
        }
    }
}