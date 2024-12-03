package com.kawaidev.kawaime.ui.adapters.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Schedule
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment

class ScheduleAdapter(private val fragment: Fragment, private var items: List<Schedule>, private var onAgain: (() -> Unit?)? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SCHEDULE = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_EMPTY = 2
        const val VIEW_TYPE_ERROR = 3
    }

    private var state: AdapterState = AdapterState.LOADING

    enum class AdapterState {
        LOADING, EMPTY, ERROR, DATA
    }

    override fun getItemViewType(position: Int): Int {
        return when (state) {
            AdapterState.LOADING -> VIEW_TYPE_LOADING
            AdapterState.EMPTY -> VIEW_TYPE_EMPTY
            AdapterState.ERROR -> VIEW_TYPE_ERROR
            AdapterState.DATA -> VIEW_TYPE_SCHEDULE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                inflater.inflate(R.layout.loading_view, parent, false)
            )
            VIEW_TYPE_EMPTY -> EmptyViewHolder(
                inflater.inflate(R.layout.empty_view, parent, false)
            )
            VIEW_TYPE_ERROR -> ErrorViewHolder(
                inflater.inflate(R.layout.error_view, parent, false)
            )
            VIEW_TYPE_SCHEDULE -> ScheduleViewHolder(
                inflater.inflate(R.layout.item_schedule, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return when (state) {
            AdapterState.LOADING, AdapterState.EMPTY, AdapterState.ERROR -> 1
            AdapterState.DATA -> items.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduleViewHolder -> holder.bind(items[position])
            is ErrorViewHolder -> holder.bind()
            is EmptyViewHolder -> holder.bind()
        }
    }

    fun setItems(newItems: List<Schedule>) {
        items = newItems
        state = if (newItems.isEmpty()) AdapterState.EMPTY else AdapterState.DATA
        notifyDataSetChanged()
    }

    fun setLoading() {
        if (state != AdapterState.LOADING) {
            state = AdapterState.LOADING
            notifyDataSetChanged()
        }
    }

    fun setError() {
        state = AdapterState.ERROR
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val time: TextView = itemView.findViewById(R.id.time)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val toEpisode: Button = itemView.findViewById(R.id.watchButton)

        fun bind(schedule: Schedule) {
            time.text = schedule.time
            title.text = schedule.name

            toEpisode.text = itemView.context.getString(R.string.episode, schedule.episode.toString())

            toEpisode.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", schedule.id)
                }

                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", schedule.id)
                }

                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.findViewById<TextView>(R.id.emptyText).text = "No schedules found"
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
                onAgain?.invoke()
            }
        }
    }
}