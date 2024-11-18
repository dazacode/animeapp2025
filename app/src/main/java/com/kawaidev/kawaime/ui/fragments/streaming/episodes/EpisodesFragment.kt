package com.kawaidev.kawaime.ui.fragments.streaming.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.streaming.EpisodesAdapter
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.helpers.EpisodesCalls
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.helpers.EpisodesUtils
import com.kawaidev.kawaime.ui.listeners.WatchedListener
import com.kawaidev.kawaime.ui.models.details.episodes.EpisodesViewModel
import com.kawaidev.kawaime.ui.models.details.episodes.EpisodesViewModelFactory
import icepick.Icepick
import icepick.State

class EpisodesFragment : Fragment(), WatchedListener {
    private lateinit var adapter: EpisodesAdapter
    lateinit var episodesCalls: EpisodesCalls
    lateinit var prefs: Prefs
    val viewModel: EpisodesViewModel by lazy {
        val service = StreamingService.create()
        ViewModelProvider(this, EpisodesViewModelFactory(service))[EpisodesViewModel::class.java]
    }

    private var id: String = ""
    private var title: String = ""

    @State private var isCollapsed: Boolean = false
    @State private var isFetched: Boolean = false
    @State private var isReversed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Icepick.restoreInstanceState(this, savedInstanceState)

        arguments?.let {
            id = it.getString(ID) ?: ""
            title = it.getString(TITLE) ?: ""
        }

        prefs = App.prefs
        prefs.setWatchedListener(this)

        episodesCalls = EpisodesCalls(this)

        adapter = EpisodesAdapter(this, emptyList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_episodes, container, false)

        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        view.findViewById<TextView>(R.id.title).text = title
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        EpisodesUtils.setupRecyclerView(recyclerView, adapter, requireContext())

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)
        EpisodesUtils.setupAppBarListener(appBarLayout) { isCollapsed ->
            this@EpisodesFragment.isCollapsed = isCollapsed
        }

        val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refresh)
        refresh.setColorSchemeResources(R.color.swipeColor, R.color.swipeColorAccent)
        refresh.setOnRefreshListener { fetchEpisodes(true) }

        if (!isFetched) fetchEpisodes()

        if (isCollapsed) {
            appBarLayout.setExpanded(false, false)
        } else {
            appBarLayout.setExpanded(true, false)
        }

        EpisodesUtils.handleReverse(view, isReversed) {
            isReversed = it
            adapter.reverse()
        }

        return view
    }

    private fun fetchEpisodes(isRefresh: Boolean = false) {
        viewModel.fetchEpisodes(id, onError = { error ->
            (activity as? MainActivity)?.dialogs?.onError(error)
        }, onLoading = { loading ->
            if (isRefresh) {
                view?.findViewById<SwipeRefreshLayout>(R.id.refresh)?.isRefreshing = loading
            } else {
                adapter.setLoading(loading)
            }
        }, onResult = { episodes ->
            adapter.setEpisodes(episodes.episodes ?: emptyList())
            isFetched = true
        })
    }

    override fun onChange() {
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    companion object {
        const val ID = "id"
        const val TITLE = "title"
    }
}