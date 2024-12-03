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
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeAdapterState
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeViewType
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.diffs.AnimeDiffCallback
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.LoadImage

class AnimeAdapter(
    var animeParams: AnimeParams,
    private var onAgain: (() -> Unit?)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemId(position: Int): Long {
        return if (position < animeParams.animeList.size) {
            animeParams.animeList[position].id.hashCode().toLong()
        } else {
            RecyclerView.NO_ID
        }
    }

    init {
        setHasStableIds(true)
    }

    private var state: AnimeAdapterState = AnimeAdapterState.DATA

    override fun getItemCount(): Int {
        return AnimeViewType.getItemCount(animeParams.animeList, state)
    }

    override fun getItemViewType(position: Int): Int {
        return AnimeViewType.getItemViewType(position, animeParams.animeList, state)
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
        fun bind(anime: BasicRelease) {
            LoadImage().loadImage(itemView.context, anime.poster, itemView.findViewById(R.id.animeImage))

            val rating = anime.rating
            val adultRatings = listOf("R", "R+", "Rx", "18+")
            itemView.findViewById<CardView>(R.id.ratingCard).visibility = if (rating != null && rating in adultRatings) View.VISIBLE else View.GONE

            itemView.findViewById<FrameLayout>(R.id.imageButton).setOnClickListener {
                val bundle = Bundle().apply { putString("id", anime.id) }
                val detailsFragment = DetailsFragment().apply { arguments = bundle }
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

    fun updateData(newAnimeList: List<BasicRelease?>) {
        val cleanNewAnimeList = newAnimeList.filterNotNull()

        clearSpecialStateItem()

        val diffCallback = AnimeDiffCallback(animeParams.animeList, cleanNewAnimeList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        if (state == AnimeAdapterState.LOADING || (cleanNewAnimeList.isNotEmpty() && animeParams.nextPage)) setBottomLoading()

        state = if (cleanNewAnimeList.isEmpty()) {
            AnimeAdapterState.EMPTY
        } else {
            AnimeAdapterState.DATA
        }

        animeParams.animeList = cleanNewAnimeList

        diffResult.dispatchUpdatesTo(this)

        if (state == AnimeAdapterState.EMPTY) {
            notifyItemInserted(0)
        }
    }

    fun setLoading() {
        if (state == AnimeAdapterState.LOADING || (animeParams.animeList.isNotEmpty() && animeParams.nextPage)) {
            setBottomLoading()
            return
        }

        clearSpecialStateItem()

        val previousState = state
        state = AnimeAdapterState.LOADING

        if (previousState != AnimeAdapterState.LOADING) {
            if (animeParams.animeList.isEmpty()) {
                notifyItemInserted(0)
            }
        }
    }

    fun setError() {
        if (state == AnimeAdapterState.ERROR) return

        clearSpecialStateItem()

        val previousState = state
        state = AnimeAdapterState.ERROR

        if (previousState != AnimeAdapterState.ERROR) {
            if (animeParams.animeList.isEmpty()) {
                notifyItemInserted(0)
            }
        }
    }

    fun setBottomLoading() {
        if (state == AnimeAdapterState.BOTTOM_LOADING) return

        clearSpecialStateItem()

        val previousState = state
        state = AnimeAdapterState.BOTTOM_LOADING
        if (previousState != AnimeAdapterState.BOTTOM_LOADING) {
            notifyItemInserted(animeParams.animeList.size)
        }
    }

    private fun clearSpecialStateItem() {
        when (state) {
            AnimeAdapterState.LOADING, AnimeAdapterState.ERROR, AnimeAdapterState.EMPTY -> {
                if (animeParams.animeList.isEmpty() && itemCount > 0) {
                    notifyItemRemoved(0)
                }
            }
            AnimeAdapterState.BOTTOM_LOADING -> {
                if (animeParams.animeList.isNotEmpty() && itemCount > animeParams.animeList.size) {
                    notifyItemRemoved(animeParams.animeList.size)
                }
            }
            else -> Unit
        }
    }

    fun setNextPage(nextPage: Boolean) {
        animeParams.nextPage = nextPage
    }
}