package com.kawaidev.kawaime.ui.adapters.streaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.streaming.EpisodeDetail
import com.kawaidev.kawaime.ui.fragments.streaming.EpisodesFragment

class EpisodesAdapter(
    private val fragment: EpisodesFragment,
    private var episodes: List<EpisodeDetail> = emptyList(),
    private var isLoading: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val LOADING_VIEW = 0
        const val EPISODES_VIEW = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LOADING_VIEW -> LoadingViewHolder(inflater.inflate(R.layout.loading_view, parent, false))
            EPISODES_VIEW -> EpisodesViewHolder(inflater.inflate(R.layout.genre_item, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) 1 else episodes.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) LOADING_VIEW else EPISODES_VIEW
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> holder.bind()
            is EpisodesViewHolder -> holder.bind(episodes[position])
        }
    }

    fun setEpisodes(newEpisodes: List<EpisodeDetail>) {
        this.episodes = newEpisodes
        this.isLoading = false
        notifyDataSetChanged()
    }

    fun setLoading(loading: Boolean) {
        this.isLoading = loading
        notifyDataSetChanged()
    }

    fun reverse() {
        episodes = episodes.reversed()
        notifyDataSetChanged()
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            // Bind data for loading view if necessary
        }
    }

    inner class EpisodesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.genre)

        fun bind(episode: EpisodeDetail) {
            text.text = episode.number.toString()
            itemView.findViewById<MaterialCardView>(R.id.genreCard).setOnClickListener {
                episode.episodeId?.let { it1 -> fragment.getServers(it1) }
            }
        }
    }
}