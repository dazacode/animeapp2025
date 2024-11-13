package com.kawaidev.kawaime.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.GenreAdapter
import com.kawaidev.kawaime.ui.custom.GridSpacingItemDecoration
import icepick.Icepick
import icepick.State

class SearchExploreFragment : Fragment() {
    private lateinit var adapter: GenreAdapter
    private lateinit var strings: Strings

    @State private var isAppBarHidden: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        Icepick.restoreInstanceState(this, savedInstanceState)

        strings = Strings()
        adapter = GenreAdapter(this, strings.genres.toList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_explore, container, false)

        val searchBar: LinearLayout = view.findViewById(R.id.searchBar)
        val searchIcon: ImageView = view.findViewById(R.id.search_icon)

        listOf(searchBar, searchIcon).forEach {
            it.setOnClickListener {
                val searchFragment = SearchFragment()
                (activity as MainActivity).pushFragment(searchFragment)
            }
        }

        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        recycler.apply {
            post {
                val spanCount = 3

                val gridLayoutManager = GridLayoutManager(context, spanCount)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (this@SearchExploreFragment.adapter.getItemViewType(position)) {
                            GenreAdapter.HEADER_VIEW -> spanCount
                            else -> 1
                        }
                    }
                }

                layoutManager = gridLayoutManager

                if (itemDecorationCount == 0) {
                    addItemDecoration(GridSpacingItemDecoration(spanCount, 4, true, isHeaderEnabled = true))
                }
            }

            adapter = this@SearchExploreFragment.adapter

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)

        if (isAppBarHidden) {
            appBarLayout.setExpanded(false, false)
        } else {
            appBarLayout.setExpanded(true, false)
        }

        setupAppBarListener(appBarLayout)

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

    companion object {

    }
}