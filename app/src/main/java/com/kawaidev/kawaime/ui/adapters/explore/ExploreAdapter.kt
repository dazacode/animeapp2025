package com.kawaidev.kawaime.ui.adapters.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.explore.helpers.ExploreViewType
import com.kawaidev.kawaime.ui.adapters.helpers.VerticalNewsRecycler
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.ui.fragments.explore.ExploreFragment
import com.kawaidev.kawaime.ui.fragments.explore.schedule.ScheduleFragment
import com.kawaidev.kawaime.ui.fragments.result.CategoryFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExploreAdapter(
    private val fragment: ExploreFragment,
    private var news: List<News>,
    private var isLoading: Boolean = false,
    private var isError: Boolean = false,
    private var onAgain: (() -> Unit?)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ExploreViewType.VIEW_TYPE_ACTION -> {
                val view = inflater.inflate(R.layout.item_action, parent, false)
                ActionViewHolder(view)
            }
            ExploreViewType.VIEW_TYPE_DIVIDER -> {
                val view = inflater.inflate(R.layout.item_divider, parent, false)
                DividerViewHolder(view)
            }
            ExploreViewType.VIEW_TYPE_LATEST_NEWS -> {
                val view = inflater.inflate(R.layout.anime_view, parent, false)
                LatestNewsViewHolder(view)
            }
            ExploreViewType.VIEW_TYPE_LOADING -> {
                val view = inflater.inflate(R.layout.bottom_loading_view, parent, false)
                LoadingViewHolder(view)
            }
            ExploreViewType.VIEW_TYPE_ERROR -> {
                val view = inflater.inflate(R.layout.error_view, parent, false)
                ErrorViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActionViewHolder -> holder.bind()
            is DividerViewHolder -> holder.bind()
            is LatestNewsViewHolder -> holder.bind()
            is LoadingViewHolder -> holder.bind()
            is ErrorViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ExploreViewType.getItemViewType(isLoading, isError, position)
    }

    override fun getItemCount(): Int {
        return ExploreViewType.getItemCount()
    }

    inner class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val popularButton: LinearLayout = itemView.findViewById(R.id.popularButtonClickable)
        private val scheduleButton: LinearLayout = itemView.findViewById(R.id.scheduleButtonClickable)
        private val collectionsButton: LinearLayout = itemView.findViewById(R.id.collectionsButtonClickable)
        private val filterButton: LinearLayout = itemView.findViewById(R.id.filterButtonClickable)
        private val recentButton: LinearLayout = itemView.findViewById(R.id.recentButtonClickable)

        fun bind() {
            popularButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("category", "top-airing")
                    putString("title", "Popular")
                }

                val categoryFragment = CategoryFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(categoryFragment)
            }

            recentButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("category", "recently-updated")
                    putString("title", "Recently updated")
                }

                val categoryFragment = CategoryFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(categoryFragment)
            }

            scheduleButton.setOnClickListener {
                val scheduleFragment = ScheduleFragment()

                (fragment.requireActivity() as MainActivity).pushFragment(scheduleFragment)
            }
        }
    }

    inner class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {}
    }

    inner class LatestNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = itemView.context.getString(R.string.latest_news)
            
            VerticalNewsRecycler.setup(fragment, news, itemView)
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {}
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val helpButton: Button = view.findViewById(R.id.help_button)
        private val tryAgainButton: Button = view.findViewById(R.id.again_button)

        fun bind() {
            helpButton.setOnClickListener {
                (fragment.requireActivity() as MainActivity).dialogs.onHelp()
            }

            tryAgainButton.setOnClickListener {
                onAgain?.invoke()
            }
        }
    }

    fun setNews(newNews: List<News>) {
        this.news = newNews
        if (!isLoading && !isError) {
            notifyItemChanged(2)
        }
    }

    fun setLoading(loading: Boolean) {
        if (this.isLoading != loading) {
            this.isLoading = loading
            notifyItemChanged(2)
        }
    }

    fun setError(error: Boolean) {
        if (this.isError != error) {
            this.isError = error
            notifyItemChanged(2)
        }
    }
}