package com.kawaidev.kawaime.ui.adapters.anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeViewType
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.diffs.AnimeDiffCallback
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.LoadImage

class AnimeAdapter(
    var animeParams: AnimeParams,
    private var onAgain: (() -> Unit?)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return AnimeViewType.getItemCount(animeParams.animeList, animeParams.isLoading, animeParams.isError, animeParams.isEmpty, animeParams.nextPage)
    }

    override fun getItemViewType(position: Int): Int {
        return AnimeViewType.getItemViewType(position, animeParams.animeList, animeParams.isLoading, animeParams.isError, animeParams.isEmpty, animeParams.nextPage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            AnimeViewType.VIEW_TYPE_LOADING -> LoadingViewHolder(inflater.inflate(R.layout.loading_view, parent, false))
            AnimeViewType.VIEW_TYPE_ERROR -> ErrorViewHolder(inflater.inflate(R.layout.error_view, parent, false))
            AnimeViewType.VIEW_TYPE_EMPTY -> EmptyViewHolder(inflater.inflate(R.layout.empty_view, parent, false))
            AnimeViewType.VIEW_TYPE_BOTTOM_LOADING -> BottomLoadingViewHolder(inflater.inflate(R.layout.bottom_loading_view, parent, false))
            else -> AnimeViewHolder(inflater.inflate(R.layout.anime_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnimeViewHolder -> holder.bind(animeParams.animeList[position])
            is LoadingViewHolder -> holder.bind()
            is ErrorViewHolder -> holder.bind()
            is EmptyViewHolder -> holder.bind()
            is BottomLoadingViewHolder -> holder.bind()
        }
    }

    inner class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(anime: SearchResponse) {
            LoadImage().loadImage(itemView.context, anime.poster, itemView.findViewById(R.id.animeImage))

            val rating = anime.rating
            val adultRatings = listOf("R", "R+", "Rx", "18+")

            if (rating != null && rating in adultRatings) {
                itemView.findViewById<CardView>(R.id.ratingCard).visibility = View.VISIBLE
            } else {
                itemView.findViewById<CardView>(R.id.ratingCard).visibility = View.GONE
            }

            itemView.findViewById<FrameLayout>(R.id.imageButton).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", anime.id)
                }
                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (animeParams.fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val helpButton: Button = view.findViewById(R.id.help_button)
        private val tryAgainButton: Button = view.findViewById(R.id.again_button)

        fun bind() {
            helpButton.setOnClickListener {
                (animeParams.fragment.requireActivity() as MainActivity).dialogs.onHelp()
            }

            tryAgainButton.setOnClickListener {
                onAgain?.invoke()
            }
        }
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.findViewById<TextView>(R.id.emptyText).text = animeParams.emptyMessage
        }
    }

    inner class BottomLoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    fun setLoading(isLoading: Boolean) {
        val previousState = animeParams.isLoading
        animeParams.isLoading = isLoading

        if (animeParams.animeList.isEmpty()) {
            if (previousState != isLoading) {
                if (isLoading) {
                    if (animeParams.animeList.size >= 0) notifyItemInserted(0)
                } else {
                    if (animeParams.animeList.size >= 0) notifyItemRemoved(0)
                }
            }
        } else {
            if (previousState != isLoading && animeParams.nextPage) {
                val position = animeParams.animeList.size
                if (isLoading) {
                    notifyItemInserted(position)
                } else {
                    notifyItemRemoved(position)
                }
            }
        }
    }


    fun setEmpty(empty: Boolean) {
        val previousState = animeParams.isEmpty
        animeParams.isEmpty = empty

        if (animeParams.animeList.isEmpty() && previousState != empty) {
            if (empty) {
                notifyItemInserted(0)
            } else {
                notifyItemRemoved(0)
            }
        }
    }

    fun setNextPage(hasNextPage: Boolean) {
        val previousState = animeParams.nextPage
        animeParams.nextPage = hasNextPage

        if (animeParams.isLoading && previousState != hasNextPage) {
            if (hasNextPage) {
                notifyItemInserted(animeParams.animeList.size)
            } else {
                notifyItemRemoved(animeParams.animeList.size)
            }
        }
    }

    fun setError(error: Boolean) {
        val previousState = animeParams.isError
        animeParams.isError = error

        if (animeParams.animeList.isEmpty() && previousState != error) {
            if (error) {
                notifyItemInserted(0)
            } else {
                notifyItemRemoved(0)
            }
        }
    }

    fun clearData() {
        updateData(emptyList())
    }

    fun updateData(newAnimeList: List<SearchResponse?>) {
        val cleanNewAnimeList = newAnimeList.filterNotNull()

        val diffCallback = AnimeDiffCallback(animeParams.animeList, cleanNewAnimeList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        animeParams.animeList = cleanNewAnimeList

        diffResult.dispatchUpdatesTo(this)
    }
}