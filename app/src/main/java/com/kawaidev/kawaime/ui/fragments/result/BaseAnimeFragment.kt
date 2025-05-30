package com.kawaidev.kawaime.ui.fragments.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
// import icepick.Icepick  // Temporarily disabled for hot reload
// import icepick.State    // Temporarily disabled for hot reload
import kotlin.math.abs

abstract class BaseAnimeFragment : Fragment() {
    protected lateinit var service: AnimeService
    protected lateinit var adapter: AnimeAdapter
    protected lateinit var recycler: RecyclerView

    // @State protected var isLoading = false                        // Temporarily disabled for hot reload
    // @State protected var isEmpty = false                          // Temporarily disabled for hot reload
    // @State protected var hasNextPage = false                      // Temporarily disabled for hot reload
    // @State protected var error: Exception? = null                 // Temporarily disabled for hot reload
    // @State protected var isAppBarHidden: Boolean = false          // Temporarily disabled for hot reload
    // @State protected var anime: List<BasicRelease> = emptyList()  // Temporarily disabled for hot reload
    // @State protected var page: Int = 1                            // Temporarily disabled for hot reload
    // @State protected var isFetched: Boolean = false               // Temporarily disabled for hot reload
    protected var isLoading = false
    protected var isEmpty = false
    protected var hasNextPage = false
    protected var error: Exception? = null
    protected var isAppBarHidden: Boolean = false
    protected var anime: List<BasicRelease> = emptyList()
    protected var page: Int = 1
    protected var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Icepick.restoreInstanceState(this, savedInstanceState)  // Temporarily disabled for hot reload
        service = AnimeService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        recycler = view.findViewById(R.id.recycler)
        setupRecyclerView()

        val back: ImageButton = view.findViewById(R.id.back_button)
        back.setOnClickListener { (activity as? MainActivity)?.popFragment() }

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)
        setupAppBarListener(appBarLayout)

        return view
    }

    private fun setupRecyclerView() {
        recycler.apply {
            post { GridRecycler.setup(requireContext(), this@BaseAnimeFragment.adapter, this, anime) }
            adapter = this@BaseAnimeFragment.adapter
            addOnScrollListener(scrollListener())
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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
            if (!isLoading && hasNextPage) {
                if (totalItemCount <= lastVisibleItem + 5) {
                    fetchAnimeData(page = page + 1)
                }
            }
        }
    }

    private fun setupAppBarListener(appBar: AppBarLayout) {
        appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
            isAppBarHidden = isCollapsed
        }
    }

    protected abstract fun fetchAnimeData(page: Int)
}