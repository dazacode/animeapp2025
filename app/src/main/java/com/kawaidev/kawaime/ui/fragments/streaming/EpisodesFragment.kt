package com.kawaidev.kawaime.ui.fragments.streaming

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.StreamingParams
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.network.interfaces.StreamingService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.activity.PlayerActivity
import com.kawaidev.kawaime.ui.adapters.GenreAdapter
import com.kawaidev.kawaime.ui.adapters.streaming.EpisodesAdapter
import com.kawaidev.kawaime.ui.custom.GridSpacingItemDecoration
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EpisodesFragment : Fragment() {
    private lateinit var service: StreamingService
    private lateinit var adapter: EpisodesAdapter

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    @State private var isAppBarHidden: Boolean = false

    private var id: String = ""
    private var title: String = ""
    @State private var episodes: Episodes = Episodes()
    @State private var isFetched: Boolean = false
    @State private var isReversed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ID) ?: ""
            title = it.getString(TITLE) ?: ""
        }

        Icepick.restoreInstanceState(this, savedInstanceState)

        service = StreamingService.create()
        adapter = EpisodesAdapter(this, episodes.episodes ?: emptyList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_episodes, container, false)

        val back: ImageButton = view.findViewById(R.id.back_button)

        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        recycler = view.findViewById(R.id.recycler)
        refresh = view.findViewById(R.id.refresh)

        recycler.apply {
            post {
                val spanCount = 5

                val gridLayoutManager = GridLayoutManager(context, spanCount)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (this@EpisodesFragment.adapter.getItemViewType(position)) {
                            EpisodesAdapter.LOADING_VIEW -> spanCount
                            else -> 1
                        }
                    }
                }

                layoutManager = gridLayoutManager

                if (itemDecorationCount == 0) {
                    addItemDecoration(GridSpacingItemDecoration(spanCount, 4, true))
                }
            }

            adapter = this@EpisodesFragment.adapter

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        view.findViewById<TextView>(R.id.title).text = title

        if (!isFetched) {
            getEpisodes()
        }

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            getEpisodes(true)
        }

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)

        if (isAppBarHidden) {
            appBarLayout.setExpanded(false, false)
        } else {
            appBarLayout.setExpanded(true, false)
        }

        setupAppBarListener(appBarLayout)

        val switchButton: ImageButton = view.findViewById(R.id.switch_button)
        switchButton.setOnClickListener {
            isReversed = !isReversed
            adapter.reverse()

            val drawableId = if (isReversed) R.drawable.switch_right else R.drawable.switch_left
            switchButton.setImageResource(drawableId)
        }

        val drawableId = if (isReversed) R.drawable.switch_right else R.drawable.switch_left
        switchButton.setImageResource(drawableId)

        if (isReversed) {
            adapter.reverse()
        }

        return view
    }

    private fun setupAppBarListener(appBarLayout: AppBarLayout) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) == appBarLayout.totalScrollRange
            isAppBarHidden = isCollapsed
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    private fun getEpisodes(isRefresh: Boolean = false) {
        when(isRefresh) {
            true -> refresh.isRefreshing = true
            false -> adapter.setLoading(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val episodes = service.getEpisodes(id)

                adapter.setEpisodes(episodes.episodes ?: emptyList())
            } catch (e: Exception) {
            } finally {
                isFetched = true

                when(isRefresh) {
                    true -> refresh.isRefreshing = false
                    false -> adapter.setLoading(false)
                }
            }
        }
    }

    fun getServers(id: String) {
        val activity: MainActivity = requireActivity() as MainActivity

        activity.dialogs.onLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val servers = service.getServers(id)

                activity.bottomSheets.onBottomSheet(servers, this@EpisodesFragment)
            } catch (e: Exception) {
            } finally {
                activity.dialogs.onLoading(false)
            }
        }
    }

    fun getStreaming(streamingParams: StreamingParams) {
        val activity: MainActivity = requireActivity() as MainActivity

        activity.dialogs.onLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val streaming = service.getStreaming(streamingParams)

                println(Json.encodeToString(streaming))

                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("streaming", Json.encodeToString(streaming))
                startActivity(intent)
            } catch (e: Exception) {
            } finally {
                activity.dialogs.onLoading(false)
            }
        }
    }

    companion object {
        const val ID = "id"
        const val TITLE = "title"
    }
}