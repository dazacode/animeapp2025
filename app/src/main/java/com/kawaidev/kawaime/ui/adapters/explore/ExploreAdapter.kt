package com.kawaidev.kawaime.ui.adapters.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.explore.helpers.ActionItem
import com.kawaidev.kawaime.ui.adapters.explore.helpers.ExploreViewType
import com.kawaidev.kawaime.ui.adapters.helpers.VerticalNewsRecycler
import com.kawaidev.kawaime.ui.fragments.explore.ExploreFragment
import com.kawaidev.kawaime.ui.fragments.explore.schedule.ScheduleFragment
import com.kawaidev.kawaime.ui.fragments.filter.FilterFragment
import com.kawaidev.kawaime.ui.fragments.result.CategoryFragment
import com.kawaidev.kawaime.utils.Converts

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
        fun bind() {
            val actions = listOf(
                ActionItem(
                    label = "Popular",
                    iconResId = R.drawable.fire,
                    backgroundColorResId = R.color.buttonColor1,
                    tintColorResId = R.color.iconColorButton1
                ) {
                    val bundle = Bundle().apply {
                        putString("CATEGORY", "top-airing")
                        putString("title", "Popular")
                    }
                    val categoryFragment = CategoryFragment().apply { arguments = bundle }
                    (fragment.requireActivity() as MainActivity).pushFragment(categoryFragment)
                },
                ActionItem(
                    label = "Schedule",
                    iconResId = R.drawable.event_note,
                    backgroundColorResId = R.color.buttonColor2,
                    tintColorResId = R.color.iconColorButton2
                ) {
                    val scheduleFragment = ScheduleFragment()
                    (fragment.requireActivity() as MainActivity).pushFragment(scheduleFragment)
                },
                ActionItem(
                    label = "Collections",
                    iconResId = R.drawable.view_day,
                    backgroundColorResId = R.color.buttonColor3,
                    tintColorResId = R.color.iconColorButton3
                ) {

                },
                ActionItem(
                    label = "Filter",
                    iconResId = R.drawable.tune,
                    backgroundColorResId = R.color.buttonColor4,
                    tintColorResId = R.color.iconColorButton4
                ) {
                    val filterFragment = FilterFragment()
                    (fragment.requireActivity() as MainActivity).pushFragment(filterFragment)
                },
                ActionItem(
                    label = "Recently Updated",
                    iconResId = R.drawable.update,
                    backgroundColorResId = R.color.buttonColor5,
                    tintColorResId = R.color.iconColorButton5
                ) {
                    val bundle = Bundle().apply {
                        putString("CATEGORY", "recently-updated")
                        putString("title", "Recently updated")
                    }
                    val categoryFragment = CategoryFragment().apply { arguments = bundle }
                    (fragment.requireActivity() as MainActivity).pushFragment(categoryFragment)
                }
            )

            val recycler: RecyclerView = itemView.findViewById(R.id.recyclerView)
            recycler.layoutManager = GridLayoutManager(itemView.context, 2)
            recycler.adapter = ActionAdapter(actions)

            recycler.apply {
                if (itemDecorationCount == 0) {
                    addItemDecoration(
                        SpacingItemDecoration(
                            Spacing(
                                horizontal = Converts.dpToPx(8f, itemView.context).toInt(),
                                vertical = Converts.dpToPx(8f, itemView.context).toInt()
                            )
                        )
                    )
                }
            }
        }
    }

    inner class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {}
    }

    inner class LatestNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
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