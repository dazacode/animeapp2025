package com.kawaidev.kawaime.ui.fragments.explore

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.network.interfaces.NewsService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.explore.ExploreAdapter
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import com.kawaidev.kawaime.utils.Converts
// import icepick.State  // Temporarily disabled for hot reload
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ExploreFragment : Fragment() {
    private lateinit var adapter: ExploreAdapter
    private lateinit var service: NewsService

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    // @State var news: List<News> = emptyList()  // Temporarily disabled for hot reload
    // @State var isFetched: Boolean = false      // Temporarily disabled for hot reload
    var news: List<News> = emptyList()
    var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        service = NewsService.create()

        adapter = ExploreAdapter(this, news) {
            getData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val space = Converts.dpToPx(8f, requireContext()).toInt()
        val edgesSpace = Converts.dpToPx(16f, requireContext()).toInt()

        recycler = view.findViewById(R.id.recycler)

        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recycler.apply {
            recycler.layoutManager = this@ExploreFragment.layoutManager
            recycler.adapter = this@ExploreFragment.adapter
        }

        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(
                SpacingItemDecoration(
                    Spacing(
                        horizontal = space,
                        vertical = space,
                        edges = Rect(edgesSpace, edgesSpace, edgesSpace, edgesSpace)
                    )
                )
            )
        }

        if (!isFetched) {
            getData()
        }

        refresh = view.findViewById(R.id.refresh)

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            getData(true)
        }

        val searchBar: LinearLayout = view.findViewById(R.id.searchBar)
        val searchIcon: ImageView = view.findViewById(R.id.search_icon)

        listOf(searchBar, searchIcon).forEach {
            it.setOnClickListener {
                val searchFragment = SearchFragment()
                (activity as MainActivity).pushFragment(searchFragment)
            }
        }

        return view
    }

    private fun getData(isRefresh: Boolean = false) {
        when(isRefresh) {
            true -> refresh.isRefreshing = true
            false -> adapter.setLoading(true)
        }
        adapter.setError(false)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (isActive) {
                    val news = service.getRecentNews().take(5)
                    if (isActive) {
                        adapter.setNews(news)
                    }
                }
            } catch (_: CancellationException) {

            } catch (e: Exception) {
                Log.e("Explore", "Error fetching news data", e)
                adapter.setError(true)
            } finally {
                if (isActive) {
                    when (isRefresh) {
                        true -> refresh.isRefreshing = false
                        false -> adapter.setLoading(false)
                    }
                    isFetched = true
                }
            }
        }
    }

    companion object {

    }
}