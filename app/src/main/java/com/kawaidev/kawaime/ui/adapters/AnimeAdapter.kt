package com.kawaidev.kawaime.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.diffs.AnimeDiffCallback
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.LoadImage

class AnimeAdapter(
    private val fragment: Fragment,
    private var animeList: List<SearchResponse>,
    private var isLoading: Boolean = false,
    private var isError: Boolean = false,
    private var isEmpty: Boolean = false,
    private var nextPage: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ERROR = 2
        const val VIEW_TYPE_EMPTY = 3
        const val VIEW_TYPE_BOTTOM_LOADING = 4
    }

    override fun getItemCount(): Int {
        return when {
            animeList.isEmpty() && (isLoading || isError) -> 1
            animeList.isEmpty() && isEmpty -> 1
            animeList.isNotEmpty() && isLoading && nextPage -> animeList.size + 1
            else -> animeList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isError && animeList.isEmpty() -> VIEW_TYPE_ERROR
            isLoading && animeList.isEmpty() -> VIEW_TYPE_LOADING
            isEmpty && animeList.isEmpty() -> VIEW_TYPE_EMPTY
            position == animeList.size && isLoading && nextPage -> VIEW_TYPE_BOTTOM_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadingViewHolder(inflater.inflate(R.layout.loading_view, parent, false))
            VIEW_TYPE_ERROR -> ErrorViewHolder(inflater.inflate(R.layout.error_view, parent, false))
            VIEW_TYPE_EMPTY -> EmptyViewHolder(inflater.inflate(R.layout.empty_view, parent, false))
            VIEW_TYPE_BOTTOM_LOADING -> BottomLoadingViewHolder(inflater.inflate(R.layout.bottom_loading_view, parent, false))
            else -> AnimeViewHolder(inflater.inflate(R.layout.anime_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnimeViewHolder -> holder.bind(animeList[position])
            is LoadingViewHolder -> holder.bind()
            is ErrorViewHolder -> holder.bind()
            is EmptyViewHolder -> holder.bind()
            is BottomLoadingViewHolder -> holder.bind()
        }
    }

    inner class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(anime: SearchResponse) {
            LoadImage().loadImage(itemView.context, anime.poster, itemView.findViewById(R.id.animeImage))

            itemView.findViewById<FrameLayout>(R.id.imageButton).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", anime.id)
                }
                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    inner class BottomLoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    fun setLoading(loading: Boolean) {
        if (isLoading != loading) {
            isLoading = loading
            notifyDataSetChanged()
        }
    }

    fun setEmpty(empty: Boolean) {
        if (isEmpty != empty) {
            this.isEmpty = empty
            notifyDataSetChanged()
        }
    }

    fun setNextPage(hasNextPage: Boolean) {
        if (nextPage != hasNextPage) {
            nextPage = hasNextPage
            notifyDataSetChanged()
        }
    }

    fun setError(error: Boolean) {
        if (isError != error) {
            isError = error
            notifyDataSetChanged()
        }
    }

    fun getNextPage(): Boolean {
        return nextPage
    }

    fun clearData() {
        updateData(emptyList())
    }

    fun updateData(newAnimeList: List<SearchResponse?>) {
        val cleanNewAnimeList = newAnimeList
            .filterNotNull()

        val diffCallback = AnimeDiffCallback(animeList, cleanNewAnimeList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        animeList = cleanNewAnimeList
        diffResult.dispatchUpdatesTo(this)
    }
}