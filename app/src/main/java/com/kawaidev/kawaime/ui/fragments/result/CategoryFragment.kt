package com.kawaidev.kawaime.ui.fragments.result

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeHelper
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch


class CategoryFragment : Fragment() {
    private lateinit var service: AnimeService
    private lateinit var adapter: AnimeAdapter
    private lateinit var recycler: RecyclerView

    private var category: String = ""
    private var title: String = ""

    @State private var isLoading = false
    @State private var isEmpty = false
    @State private var hasNextPage = false
    @State private var error: Exception? = null
    @State private var isAppBarHidden: Boolean = false
    @State private var anime: List<BasicRelease> = emptyList()
    @State private var page: Int = 1
    @State private var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Icepick.restoreInstanceState(this, savedInstanceState)

        arguments?.let {
            category = it.getString(CATEGORY) ?: ""
            title = it.getString(TITLE) ?: ""
        }

        service = AnimeService.create()

        adapter = AnimeAdapter(AnimeParams(this, anime)) {
            searchAnime(1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        view.findViewById<TextView>(R.id.title).text = title

        recycler = view.findViewById(R.id.recycler)

        val back: ImageButton = view.findViewById(R.id.back_button)

        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        val edit: Button = view.findViewById(R.id.edit_button)
        edit.visibility = View.GONE

        recycler.apply {
            post {
                GridRecycler.setup(requireContext(), this@CategoryFragment.adapter, recycler, anime)
            }
            adapter = this@CategoryFragment.adapter
            addOnScrollListener(scrollListener())

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        if (!isFetched) searchAnime(page)

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

    fun searchAnime(page: Int = 1) {
        isLoading = true
        adapter.setLoading()

        this@CategoryFragment.page = page

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (animeList, hasNextPage) = service.getCategory(category, page)

                AnimeHelper.updateGridData(adapter, this@CategoryFragment.anime + animeList, recycler)
                adapter.setNextPage(hasNextPage)

                this@CategoryFragment.hasNextPage = hasNextPage

                this@CategoryFragment.anime += animeList


            } catch (e: Exception) {
                adapter.setError()
            } finally {
                isLoading = false
                isFetched = true
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
            if (!isLoading && hasNextPage) {
                if (totalItemCount <= lastVisibleItem + 5) {
                    searchAnime(page = page + 1)
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

    companion object {
        const val CATEGORY = "CATEGORY"
        const val TITLE = "title"
    }
}