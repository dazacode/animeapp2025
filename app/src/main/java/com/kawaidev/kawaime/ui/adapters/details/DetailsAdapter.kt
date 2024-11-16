package com.kawaidev.kawaime.ui.adapters.details

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.details.helpers.DetailsHelper
import com.kawaidev.kawaime.ui.adapters.details.helpers.DetailsViewType
import com.kawaidev.kawaime.ui.adapters.diffs.ReleaseDiffCallback
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.Converts

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
        private val helpButton: FrameLayout = view.findViewById(R.id.negative_button)
        private val tryAgainButton: FrameLayout = view.findViewById(R.id.details_button)

        fun bind() {
            helpButton.setOnClickListener {
                (fragment.requireActivity() as MainActivity).dialogs.onHelp()
            }

            tryAgainButton.setOnClickListener {
                fragment.getAnime {}
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(release: Release) {
            DetailsHelper.headerBind(fragment, itemView, release)
        }
    }

    inner class DescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(description: String) {
            DetailsHelper.descBind(description, itemView, isExpanded) {
                isExpanded = it
            }
        }
    }

    inner class SeasonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(seasonItems: List<Season>) {
            val seasons = seasonItems.filter { it.isCurrent == false }

            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = "Other Seasons"

            val space = Converts.dpToPx(8f, fragment.requireContext()).toInt()

            val latestAdapter = SeasonAdapter(fragment, seasons)
            val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recycler.adapter = latestAdapter

            if (recycler.itemDecorationCount == 0) {
                recycler.addItemDecoration(
                    SpacingItemDecoration(
                        Spacing(
                            horizontal = space,
                            vertical = space,
                            edges = Rect(space, space, space, space)
                        )
                    )
                )
            }
        }
    }

    inner class RelatedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(relatedItems: List<SearchResponse>) {
            DetailsHelper.adapterBind(itemView, fragment, relatedItems, "Related")
        }
    }

    inner class RecommendationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recommendationItems: List<SearchResponse>) {
            DetailsHelper.adapterBind(itemView, fragment, recommendationItems, "Recommendations")
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
            notifyItemChanged(1)
        }
    }
}