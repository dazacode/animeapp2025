package com.kawaidev.kawaime.ui.fragments.favorite

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeViewType
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
import com.kawaidev.kawaime.ui.listeners.FavoriteListener
import com.kawaidev.kawaime.utils.Converts
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment(), FavoriteListener {
    private lateinit var adapter: AnimeAdapter
    private lateinit var prefs: Prefs
    private lateinit var service: AnimeService
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    @State private var isLoading = false
    @State private var isEmpty = false
    @State private var hasNextPage = false
    @State private var error: Exception? = null
    @State private var anime: List<SearchResponse> = emptyList()
    @State private var page: Int = 1
    @State private var isFetched: Boolean = false

    private val perPage: Int = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Icepick.restoreInstanceState(this, savedInstanceState)

        adapter = AnimeAdapter(AnimeParams(this, anime, emptyMessage = "Opps, nothing is here! \n Your favorite added anime will appear here!")) {
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

        recycler = view.findViewById(R.id.recycler)

        recycler.apply {
            post {
                GridRecycler.setup(requireContext(), this@FavoriteFragment.adapter, recycler)
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
        if (!isReload) {
            if (isRefresh) refresh.isRefreshing = true else adapter.setLoading(true)
        }
        adapter.setError(false)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val ids = prefs.getFavorites().toList()
                val totalItems = ids.size
                val start = (page - 1) * perPage
                val end = (start + perPage).coerceAtMost(totalItems)
                val currentIds = ids.subList(start, end)

                val animePage = currentIds.map { id ->
                    async {
                        try {
                            val release = service.getAnime(id)
                            convertReleaseToSearchResponse(release)
                        } catch (e: Exception) {
                            Log.e("Favorite Fragment", "Error loading anime with ID $id: ${e.message}")
                            null
                        }
                    }
                }.awaitAll()

                if (!isReload) {
                    if (isRefresh) refresh.isRefreshing = false else adapter.setLoading(false)
                }

                val newAnime = animePage.filterNotNull()

                if (isRefresh || isReload) {
                    anime = newAnime
                } else {
                    anime += newAnime
                }

                withContext(Dispatchers.Main) {
                    adapter.updateData(anime)

                    adapter.setEmpty(anime.isEmpty())
                    isEmpty = anime.isEmpty()

                    hasNextPage = end < totalItems
                    page++
                }
            } catch (e: Exception) {
                Log.e("Favorite Fragment", "Error fetching anime list: ${e.message}")
                adapter.setError(true)
            } finally {
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