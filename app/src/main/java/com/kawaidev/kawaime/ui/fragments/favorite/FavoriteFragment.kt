package com.kawaidev.kawaime.ui.fragments.favorite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.AnimeAdapter
import com.kawaidev.kawaime.ui.custom.GridSpacingItemDecoration
import com.kawaidev.kawaime.ui.listeners.FavoriteListener
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(), FavoriteListener {
    private lateinit var adapter: AnimeAdapter
    private lateinit var prefs: Prefs
    private lateinit var service: AnimeService
    private lateinit var refresh: SwipeRefreshLayout

    @State private var isLoading = false
    @State private var isEmpty = false
    @State private var hasNextPage = false
    @State private var error: Exception? = null
    @State private var isAppBarHidden: Boolean = false
    @State private var anime: List<SearchResponse> = emptyList()
    @State private var page: Int = 1
    @State private var isFetched: Boolean = false

    private val perPage: Int = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Icepick.restoreInstanceState(this, savedInstanceState)

        adapter = AnimeAdapter(this, anime,
            emptyMessage = "Opps, nothing is here! \n Your favorite added anime will appear here!") {
            getAnime()
        }
        prefs = App.prefs
        prefs.setFavoriteListener(this)

        service = AnimeService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        recycler.apply {
            post {
                val spanCount = calculateSpanCount(context)

                val gridLayoutManager = GridLayoutManager(context, spanCount)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter?.getItemViewType(position)) {
                            AnimeAdapter.VIEW_TYPE_LOADING -> spanCount
                            AnimeAdapter.VIEW_TYPE_BOTTOM_LOADING -> spanCount
                            AnimeAdapter.VIEW_TYPE_ERROR -> spanCount
                            AnimeAdapter.VIEW_TYPE_EMPTY -> spanCount
                            else -> 1
                        }
                    }
                }

                recycler.layoutManager = gridLayoutManager

                if (recycler.itemDecorationCount == 0) {
                    recycler.addItemDecoration(GridSpacingItemDecoration(spanCount, 6, true))
                }
            }
            adapter = this@FavoriteFragment.adapter
            addOnScrollListener(scrollListener())

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        refresh = view.findViewById(R.id.refresh)

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            getAnime(true)
        }

        if (!isFetched) getAnime()

        return view
    }

    private fun getAnime(isRefresh: Boolean = false, isReload: Boolean = false) {
        if (isRefresh || isReload) {
            page = 1
            anime = emptyList()
        }

        isLoading = true
        // Only show loading indicators if not reloading
        if (!isReload) {
            if (isRefresh) refresh.isRefreshing = true else adapter.setLoading(true)
        }
        adapter.setError(false)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Fetch list of favorite IDs
                val ids = prefs.getFavorites().toList()
                val totalItems = ids.size
                val start = (page - 1) * perPage
                val end = (start + perPage).coerceAtMost(totalItems)
                val currentIds = ids.subList(start, end)

                // Load each anime by ID and convert to SearchResponse
                val animePage = currentIds.map { id ->
                    async {
                        val release = service.getAnime(id)
                        convertReleaseToSearchResponse(release)
                    }
                }.awaitAll()

                anime = if (isRefresh) animePage else anime + animePage
                adapter.updateData(anime)

                adapter.setEmpty(anime.isEmpty())

                hasNextPage = end < totalItems
                page++
            } catch (e: Exception) {
                adapter.setError(true)
                error = e
            } finally {
                if (!isReload) {
                    if (isRefresh) refresh.isRefreshing = false else adapter.setLoading(false)
                }
                isLoading = false
                isFetched = true
            }
        }
    }

    private fun scrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

            // Trigger pagination when nearing the end and there's more data to load
            if (!isLoading && hasNextPage && error == null && lastVisibleItem >= totalItemCount - 5) {
                getAnime()
            }
        }
    }

    private fun calculateSpanCount(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val itemWidth = context.resources.getDimensionPixelSize(R.dimen.anime_item_width)
        return maxOf(1, screenWidth / itemWidth)
    }

    private fun convertReleaseToSearchResponse(release: Release): SearchResponse {
        val animeInfo = release.anime?.info ?: return SearchResponse()

        return SearchResponse(
            id = animeInfo.id,
            name = animeInfo.name,
            description = animeInfo.description,
            poster = animeInfo.poster,
            episodes = animeInfo.stats?.episodes,
            type = animeInfo.stats?.type,
            rating = animeInfo.stats?.rating
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    companion object {

    }

    override fun onChange() {
        getAnime(isReload = true)
    }
}