package com.kawaidev.kawaime.ui.adapters.streaming

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.streaming.EpisodeDetail
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment

class EpisodesAdapter(
    private val fragment: EpisodesFragment,
    private var episodes: Episodes = Episodes(),
    private var isLoading: Boolean = false,
    private var isError: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val LOADING_VIEW = 0
        const val EPISODES_VIEW = 1
        const val ERROR_VIEW = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LOADING_VIEW -> LoadingViewHolder(inflater.inflate(R.layout.loading_view, parent, false))
            EPISODES_VIEW -> EpisodesViewHolder(inflater.inflate(R.layout.small_button_item, parent, false))
            ERROR_VIEW -> ErrorViewHolder(inflater.inflate(R.layout.error_view, parent, false)) // Inflate error view
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return when {
            isLoading -> 1
            isError -> 1
            else -> episodes.episodes?.size ?: 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> LOADING_VIEW
            isError -> ERROR_VIEW
            else -> EPISODES_VIEW
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> holder.bind()
            is EpisodesViewHolder -> holder.bind(episodes.episodes!![position])
            is ErrorViewHolder -> holder.bind()
        }
    }

    fun setEpisodes(newEpisodes: Episodes) {
        this.episodes = newEpisodes
        this.isLoading = false
        this.isError = false
        notifyDataSetChanged()
    }

    fun setLoading(loading: Boolean) {
        this.isLoading = loading
        notifyDataSetChanged()
    }

    fun setError(error: Boolean) {
        this.isError = error
        notifyDataSetChanged()
    }

    fun reverse() {
        episodes.episodes = episodes.episodes?.reversed()
        notifyDataSetChanged()
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {}
    }

    inner class EpisodesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.genre)

        fun bind(episode: EpisodeDetail) {
            text.text = episode.number.toString()

            val watchedEpisode = episode.episodeId?.let { fragment.prefs.findByEpisodeId(it) }

            if (watchedEpisode != null) {
                text.paintFlags = text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                text.setTextColor(Color.RED)
            } else {
                text.paintFlags = text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                text.setTextColor(ContextCompat.getColor(itemView.context, R.color.text3))
            }

            itemView.findViewById<MaterialCardView>(R.id.genreCard).setOnClickListener {
                episode.episodeId?.let { it1 -> fragment.episodesCalls.getServers(it1, episodes) }
            }
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
                fragment.fetchEpisodes()
            }
        }
    }
}