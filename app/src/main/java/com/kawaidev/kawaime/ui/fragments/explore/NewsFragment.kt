package com.kawaidev.kawaime.ui.fragments.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.news.News
import com.kawaidev.kawaime.network.interfaces.NewsService
import com.kawaidev.kawaime.ui.adapters.explore.NewsAdapter
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewsFragment : Fragment() {
    private var news: List<News> = emptyList()
    private lateinit var newsAdapter: NewsAdapter
    private var isLoading: Boolean = false
    private var isEmpty: Boolean = false
    private var isAppBarHidden: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            val json = it.getString("news")
            news = json?.let { jsonString ->
                Json.decodeFromString<List<News>>(jsonString)
            } ?: emptyList()

            isAppBarHidden = it.getBoolean("appbar")
        }

        newsAdapter = NewsAdapter(news, isLoading, isEmpty)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appBarLayout)

        if (isAppBarHidden) {
            appBarLayout.setExpanded(false, false)
        } else {
            appBarLayout.setExpanded(true, false)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = newsAdapter

        val backButton: ImageView = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        fetchNews()

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
        val json = Json.encodeToString(news)
        outState.putString("news", json)
        outState.putBoolean("appbar", isAppBarHidden)
    }

    private fun fetchNews() {
        isLoading = true
        newsAdapter.setLoading(isLoading)

        lifecycleScope.launch {
            val newsList = NewsService.create().getRecentNews()
            updateNews(newsList)
        }
    }

    private fun updateNews(newsList: List<News>) {
        news = newsList

        isLoading = false
        isEmpty = news.isEmpty()

        newsAdapter.setLoading(isLoading)
        newsAdapter.setEmpty(isEmpty)
        newsAdapter.setData(news)
    }

    companion object {

    }
}