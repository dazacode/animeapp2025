package com.kawaidev.kawaime.ui.fragments.result

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ResultFragment : Fragment() {
    private lateinit var service: AnimeService
    private lateinit var adapter: AnimeAdapter
    private lateinit var recycler: RecyclerView

    private var searchParams: SearchParams = SearchParams()

    @State private var isLoading = false
    @State private var isEmpty = false
    @State private var hasNextPage = false
    @State private var error: Exception? = null
    @State private var isAppBarHidden: Boolean = false
    @State private var anime: List<BasicRelease> = emptyList()
    @State private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Icepick.restoreInstanceState(this, savedInstanceState)

        arguments?.let {
            searchParams = Json.decodeFromString(it.getString(SEARCH_PARAMS) ?: "")
        }

        service = AnimeService.create()

        adapter = AnimeAdapter(AnimeParams(this, anime)) {
            searchAnime(searchParams, 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        recycler = view.findViewById(R.id.recycler)

        val back: ImageButton = view.findViewById(R.id.back_button)

        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        recycler.apply {
            post {
                GridRecycler.setup(requireContext(), this@ResultFragment.adapter, recycler)
            }
            adapter = this@ResultFragment.adapter
            addOnScrollListener(scrollListener())

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        searchAnime(searchParams)

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)

        if (isAppBarHidden) {
            appBarLayout.setExpanded(false, false)
        } else {
            appBarLayout.setExpanded(true, false)
        }

        setupAppBarListener(appBarLayout)

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    fun searchAnime(searchParams: SearchParams, page: Int = 1) {
        isLoading = true
        adapter.setLoading()

        searchParams.page = page

        this@ResultFragment.page = page

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (animeList, hasNextPage) = service.searchAnime(searchParams)

                adapter.updateData(animeList)
                adapter.setNextPage(hasNextPage)

                this@ResultFragment.anime = animeList

            } catch (e: Exception) {
                adapter.setError()
                error = e
            } finally {
                isLoading = false
            }
        }
    }

    private fun scrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(null).maxOrNull() ?: 0
                else -> 0
            }
            if (!isLoading && hasNextPage && error == null) {
                if (totalItemCount <= lastVisibleItem + 5) {
                    searchAnime(searchParams, page = page + 1)
                }
            }
        }
    }

    private fun setupAppBarListener(appBarLayout: AppBarLayout) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) == appBarLayout.totalScrollRange
            isAppBarHidden = isCollapsed
        })
    }

    private fun calculateSpanCount(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val itemWidth = context.resources.getDimensionPixelSize(R.dimen.anime_item_width)
        return maxOf(1, screenWidth / itemWidth)
    }

    companion object {
        const val SEARCH_PARAMS = "search_params"
    }
}