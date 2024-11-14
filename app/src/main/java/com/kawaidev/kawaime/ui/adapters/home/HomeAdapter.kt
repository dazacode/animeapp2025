package com.kawaidev.kawaime.ui.adapters.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.diffs.HomeDiffCallback
import com.kawaidev.kawaime.ui.adapters.home.helpers.ViewTypes
import com.kawaidev.kawaime.ui.custom.LinearSpacingItemDecoration
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.ui.fragments.home.HomeFragment
import com.kawaidev.kawaime.utils.LoadImage

class HomeAdapter(
    private var fragment: HomeFragment,
    private var home: Home,
    private var isLoading: Boolean = false,
    private var isError: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return ViewTypes.getViewType(position, isLoading, isError, home)
    }

    override fun getItemCount(): Int {
        return ViewTypes.getItemCount(isLoading, isError, home)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewTypes.VIEW_TYPE_LOADING -> LoadingViewHolder(inflater.inflate(R.layout.loading_view, parent, false))
            ViewTypes.VIEW_TYPE_ERROR -> ErrorViewHolder(inflater.inflate(R.layout.error_view, parent, false))
            ViewTypes.VIEW_TYPE_SPOTLIGHT -> SpotlightViewHolder(inflater.inflate(R.layout.spotlight_view, parent, false))
            else -> AnimeViewHolder(inflater.inflate(R.layout.anime_view, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> {}
            is ErrorViewHolder -> holder.bind()
            is SpotlightViewHolder -> holder.bind(home.spotlightAnimes)
            is AnimeViewHolder -> {
                when (getItemViewType(position)) {
                    ViewTypes.VIEW_TYPE_LATEST -> holder.bind(home.latestEpisodeAnimes, "Latest")
                    ViewTypes.VIEW_TYPE_TRENDING -> holder.bind(home.trendingAnimes, "Trending")
                    ViewTypes.VIEW_TYPE_TOP_AIRING -> holder.bind(home.topAiringAnimes, "Top Airing")
                    ViewTypes.VIEW_TYPE_POPULAR -> holder.bind(home.mostPopularAnimes, "Most Popular")
                    ViewTypes.VIEW_TYPE_MOST_FAVORITE -> holder.bind(home.mostFavoriteAnimes, "Most Favorite")
                    ViewTypes.VIEW_TYPE_LATEST_COMPLETED -> holder.bind(home.latestCompletedAnimes, "Latest Completed")
                    ViewTypes.VIEW_TYPE_TOP_10_TODAY -> holder.bind(home.top10Animes.today, "Top 10 Today")
                    ViewTypes.VIEW_TYPE_TOP_10_WEEK -> holder.bind(home.top10Animes.week, "Top 10 This Week")
                    ViewTypes.VIEW_TYPE_TOP_10_MONTH -> holder.bind(home.top10Animes.month, "Top 10 This Month")
                }
            }
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val helpButton: FrameLayout = view.findViewById(R.id.negative_button)
        private val tryAgainButton: FrameLayout = view.findViewById(R.id.details_button)

        fun bind() {
            helpButton.setOnClickListener {
                (fragment.requireActivity() as MainActivity).dialogs.onHelp()
            }

            tryAgainButton.setOnClickListener {
                fragment.getHome()
            }
        }
    }

    inner class SpotlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(spotlightData: List<SearchResponse>) {
            val anime = spotlightData.first()

            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = anime.name

            val descTextView: TextView = itemView.findViewById(R.id.desc)
            descTextView.text = anime.description

            itemView.findViewById<FrameLayout>(R.id.details_button).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", anime.id)
                }
                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }

            LoadImage().loadImage(itemView.context, anime.poster, itemView.findViewById(R.id.image))
        }
    }

    inner class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(latestData: List<SearchResponse>, title: String) {
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = title

            val latestAdapter = AnimeAdapter(fragment, latestData)
            val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recycler.adapter = latestAdapter

            val snapHelper = LinearSnapHelper()

            if (recycler.onFlingListener == null) {
                snapHelper.attachToRecyclerView(recycler)
            }

            if (recycler.itemDecorationCount == 0) {
                recycler.addItemDecoration(LinearSpacingItemDecoration(8, true))
            }
        }
    }

    fun setHome(newHome: Home) {
        val diffResult = DiffUtil.calculateDiff(HomeDiffCallback(this.home, newHome))
        this.home = newHome
        diffResult.dispatchUpdatesTo(this)

        this.home.spotlightAnimes = this.home.spotlightAnimes.shuffled()

        if (this.home == newHome) notifyItemChanged(0)
    }

    fun setLoading(loading: Boolean) {
        if (loading != isLoading) {
            isLoading = loading
            notifyDataSetChanged()
        }
    }

    fun setError(error: Boolean) {
        if (error != isError) {
            isError = error
            notifyDataSetChanged()
        }
    }
}