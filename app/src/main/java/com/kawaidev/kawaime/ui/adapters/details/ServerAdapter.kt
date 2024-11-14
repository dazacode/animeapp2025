package com.kawaidev.kawaime.ui.adapters.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Server
import com.kawaidev.kawaime.ui.fragments.streaming.EpisodesFragment

class ServerAdapter(
    private val fragment: EpisodesFragment,
    private val episodeServers: EpisodeServers,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW = 0
        const val SERVERS_VIEW = 1
    }

    private var onServerSelectedListener: OnServerSelectedListener? = null

    interface OnServerSelectedListener {
        fun onServerSelected()
    }

    override fun getItemCount(): Int {
        return episodeServers.sub.size + episodeServers.dub.size + episodeServers.raw.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_VIEW else SERVERS_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW -> HeaderViewHolder(inflater.inflate(R.layout.header_item, parent, false))
            SERVERS_VIEW -> ServerViewHolder(inflater.inflate(R.layout.server_item, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind()
        } else if (holder is ServerViewHolder) {
            val (server, type) = getServerAtPosition(position)
            holder.bind(server, type)
        }
    }

    private fun getServerAtPosition(position: Int): Pair<Server, String> {
        return when {
            position < episodeServers.sub.size -> episodeServers.sub[position] to "sub"
            position < episodeServers.sub.size + episodeServers.dub.size -> episodeServers.dub[position - episodeServers.sub.size] to "dub"
            else -> episodeServers.raw[position - episodeServers.sub.size - episodeServers.dub.size] to "raw"
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            itemView.findViewById<TextView>(R.id.headerTitle).text = "Choose a server"
            itemView.findViewById<TextView>(R.id.headerNote).text = "* If a server doesn't work, please choose another. "
        }
    }

    inner class ServerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val serverNameTextView: TextView = view.findViewById(R.id.server)
        private val serverIcon: ImageView = view.findViewById(R.id.icon)

        fun bind(server: Server, type: String) {
            serverNameTextView.text = formatServerName(server.serverName ?: "")

            val iconResId = when (type) {
                "sub" -> R.drawable.caption
                "dub" -> R.drawable.voice
                "raw" -> R.drawable.raw
                else -> R.drawable.raw
            }

            serverIcon.setImageResource(iconResId)

            itemView.findViewById<MaterialCardView>(R.id.genreCard).setOnClickListener {
                fragment.getStreaming(StreamingParams(
                    animeEpisodeId = episodeServers.episodeId ?: "",
                    server = server.serverName ?: "",
                    category = type
                ))

                onServerSelectedListener?.onServerSelected()
            }
        }
    }

    private fun formatServerName(serverName: String): String {
        return when (serverName.lowercase()) {
            "hd-1" -> "HD-1"
            "hd-2" -> "HD-2"
            "streamsb" -> "StreamSB"
            "streamtape" -> "StreamTape"
            "vidcloud" -> "VidCloud"
            else -> serverName.replaceFirstChar { it.uppercase() }
        }
    }

    fun setOnServerSelectedListener(listener: OnServerSelectedListener) {
        onServerSelectedListener = listener
    }
}