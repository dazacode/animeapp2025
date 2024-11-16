package com.kawaidev.kawaime.ui.fragments.search

import android.app.Activity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputEditText
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.SearchHistoryAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.fragments.search.helpers.SearchHelpers
import com.kawaidev.kawaime.ui.listeners.SearchListener
import com.kawaidev.kawaime.ui.models.SearchViewModel
import icepick.Icepick
import icepick.State

class SearchFragment : Fragment(), SearchListener {
    private lateinit var textField: TextInputEditText
    lateinit var searchRecycler: RecyclerView
    lateinit var historyRecycler: RecyclerView
    lateinit var searchViewModel: SearchViewModel

    lateinit var micButton: ImageButton
    lateinit var clearButton: ImageButton

    lateinit var animeAdapter: AnimeAdapter
    lateinit var historyAdapter: SearchHistoryAdapter

    lateinit var prefs: Prefs

    @State private var isLoading = false
    @State private var isEmpty = false
    @State private var hasNextPage = false
    @State private var error: Exception? = null
    @State private var isAppBarHidden: Boolean = false
    @State private var anime: List<SearchResponse> = emptyList()

    var initSearch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Icepick.restoreInstanceState(this, savedInstanceState)

        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false).also {
        textField = it.findViewById(R.id.textField)
        historyRecycler = it.findViewById(R.id.historyRecycler)
        searchRecycler = it.findViewById(R.id.searchRecycler)

        animeAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        micButton = it.findViewById(R.id.mic_button)
        clearButton = it.findViewById(R.id.clear_button)

        val back: ImageButton = it.findViewById(R.id.back_button)

        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        val appBarLayout = it.findViewById<AppBarLayout>(R.id.appBarLayout)

        if (isAppBarHidden) {
            appBarLayout.setExpanded(false, false)
        } else {
            appBarLayout.setExpanded(true, false)
        }

        SearchHelpers.setupAppBar(it, this)
        setupAppBarListener(appBarLayout)
        SearchHelpers.setupRecyclerView(requireContext(), this)

        handleObserve()
    }

    private fun handleObserve() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            updateAnimeList(searchResults)
        }

        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            animeAdapter.setLoading(isLoading)
            this@SearchFragment.isLoading = isLoading
        }

        searchViewModel.hasNextPage.observe(viewLifecycleOwner) {
            animeAdapter.setNextPage(it)
            hasNextPage = it
        }

        searchViewModel.error.observe(viewLifecycleOwner) {
            error = it
            animeAdapter.setError(error != null)
        }

        searchViewModel.isEmpty.observe(viewLifecycleOwner) {
            if (textField.text.isNullOrEmpty().not()) {
                isEmpty = it
                animeAdapter.setEmpty(it)

                if (it) {
                    animeAdapter.setLoading(false)
                    animeAdapter.clearData()
                }
            }
        }
    }

    fun pasteText(search: String) {
        if (isAdded && ::textField.isInitialized) {
            textField.setText(search)
            view?.let { SearchHelpers.hideSoftKeyboard(it) }
        }
    }

    fun clearText() {
        textField.text = null
    }

    fun clearSearches() = prefs.clearSearches()

    private fun updateAnimeList(animeList: List<SearchResponse>) {
        if (animeList.isNotEmpty()) {
            animeAdapter.updateData(animeList)
            anime = animeList
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    private fun setupAppBarListener(appBarLayout: AppBarLayout) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) == appBarLayout.totalScrollRange
            isAppBarHidden = isCollapsed
        })
    }

    fun scrollListener() = object : RecyclerView.OnScrollListener() {
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
                    searchViewModel.loadNextSearch()
                }
            }
        }
    }

    private fun initialize() {
        initSearch = arguments?.getString(INIT_SEARCH)

        searchViewModel = SearchViewModel()

        prefs = App.prefs
        prefs.setSearchListener(this)

        animeAdapter = AnimeAdapter(AnimeParams(this, anime)) {
            searchViewModel.refreshSearch(textField.text.toString())
        }

        historyAdapter = SearchHistoryAdapter(this, prefs.getRecentSearches())
    }

    val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            textField.setText(results?.get(0))
        }
    }

    override fun onSearchAdded(search: List<String>) {
        historyAdapter.updateData(search)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SearchHelpers.hideSoftKeyboard(requireView())
    }

    companion object {
        private const val INIT_SEARCH = "search"
    }
}