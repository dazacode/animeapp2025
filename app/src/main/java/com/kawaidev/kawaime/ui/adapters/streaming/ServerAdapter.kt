package com.kawaidev.kawaime.ui.adapters.streaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.helpers.PlayerParams
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.dao.streaming.Server
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment

class ServerAdapter(
    private val fragment: EpisodesFragment,
    private val episodeServers: EpisodeServers,
    private val episodes: Episodes,
    var isDownload: Boolean = false,
    private val isDownloable: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW = 0
        const val SERVERS_VIEW = 1
        const val DOWNLOAD_VIEW = 2
    }

    private var onServerSelectedListener: OnServerSelectedListener? = null

    interface OnServerSelectedListener {
        fun onServerSelected()
    }

    override fun getItemCount(): Int {
        val serversCount = episodeServers.sub.size + episodeServers.dub.size + episodeServers.raw.size
        var count = serversCount + 1
        if (isDownloable) count++
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> HEADER_VIEW
            position == itemCount - 1 && isDownloable -> DOWNLOAD_VIEW
            else -> SERVERS_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW -> HeaderViewHolder(inflater.inflate(R.layout.header_item, parent, false))
            SERVERS_VIEW -> ServerViewHolder(inflater.inflate(R.layout.server_item, parent, false))
            DOWNLOAD_VIEW -> DownloadViewHolder(inflater.inflate(R.layout.download_item, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is DownloadViewHolder -> holder.bind()
            is ServerViewHolder -> {
                val (server, type) = getServerAtPosition(position - 1)
                holder.bind(server, type)
            }
        }
    }

    private fun getServerAtPosition(position: Int): Pair<Server, String> {
        return when {
            position < episodeServers.sub.size -> episodeServers.sub[position] to "sub"
            position < episodeServers.sub.size + episodeServers.dub.size -> episodeServers.dub[position - episodeServers.sub.size] to "dub"
            else -> episodeServers.raw[position - episodeServers.sub.size - episodeServers.dub.size] to "raw"
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.findViewById<TextView>(R.id.headerTitle).text = itemView.context.getString(R.string.choose_server)
            itemView.findViewById<TextView>(R.id.headerNote).text = itemView.context.getString(R.string.server_work)
        }
    }

    inner class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val genreCard: MaterialCardView = itemView.findViewById(R.id.genreCard)
        private val serverNameTextView: TextView = itemView.findViewById(R.id.server)
        private val serverIcon: ImageView = itemView.findViewById(R.id.icon)
        private val recommended: TextView = itemView.findViewById(R.id.recommended)

        fun bind() {
            serverNameTextView.text = itemView.context.getString(R.string.download_server)
            serverIcon.setImageResource(R.drawable.download)
            recommended.visibility = View.GONE

            genreCard.setOnClickListener {
                // Toggle the checked state
                genreCard.isChecked = !genreCard.isChecked
                // Update the isDownload variable based on the new state
                isDownload = genreCard.isChecked
            }
        }
    }

    inner class ServerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val serverNameTextView: TextView = view.findViewById(R.id.server)
        private val serverIcon: ImageView = view.findViewById(R.id.icon)
        private val recommended: TextView = view.findViewById(R.id.recommended)

        fun bind(server: Server, type: String) {
            serverNameTextView.text = formatServerName(server.serverName ?: "")

            val iconResId = when (type) {
                "sub" -> R.drawable.caption
                "dub" -> R.drawable.voice
                "raw" -> R.drawable.raw
                else -> R.drawable.raw
            }

            serverIcon.setImageResource(iconResId)

            val isRecommended = server.serverName in listOf("streamsb", "streamtape")

            if (isRecommended) {
                recommended.visibility = View.VISIBLE
                when (server.serverName?.lowercase()) {
                    "hd-1" -> {
                        recommended.setTextColor(ContextCompat.getColor(itemView.context, R.color.goldColor))
                    }
                    "streamsb", "streamtape" -> {
                        recommended.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                    }
                    else -> {
                        recommended.setTextColor(ContextCompat.getColor(itemView.context, R.color.goldColor))
                    }
                }
            } else {
                recommended.visibility = View.GONE
            }

            recommended.visibility = View.GONE

            itemView.findViewById<MaterialCardView>(R.id.genreCard).setOnClickListener {
                fragment.episodesCalls.getStreaming(
                    PlayerParams(
                        episodeServers.episodeId ?: "",
                        episodeServers.episodeNo.toString(),
                        fragment.title,
                        episodes,
                        server = server.serverName ?: "",
                        category = type,
                        download = isDownload
                    )
                )
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