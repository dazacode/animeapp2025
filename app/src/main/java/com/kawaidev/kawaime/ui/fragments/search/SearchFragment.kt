package com.kawaidev.kawaime.ui.fragments.search

import android.app.Activity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputEditText
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.SearchHistoryAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeHelper
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.fragments.search.helpers.SearchHelpers
import com.kawaidev.kawaime.ui.listeners.SearchListener
import com.kawaidev.kawaime.ui.models.SearchViewModel
import icepick.Icepick
import icepick.State
import kotlin.math.abs

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

    @State
    var isLoading = false
    @State private var isEmpty = false
    @State
    var hasNextPage = false
    @State
    var error: Exception? = null
    @State private var isAppBarHidden: Boolean = false
    @State private var isInit: Boolean = false
    @State private var anime: List<BasicRelease> = emptyList()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInitSearch()
    }

    private fun handleObserve() {
        searchViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            AnimeHelper.updateGridData(animeAdapter, searchResults, searchRecycler)
        }

        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) animeAdapter.setLoading()
            this@SearchFragment.isLoading = isLoading
        }

        searchViewModel.hasNextPage.observe(viewLifecycleOwner) {
            animeAdapter.setNextPage(it)
            hasNextPage = it
        }

        searchViewModel.error.observe(viewLifecycleOwner) {
            Log.e("Search fragment", it?.message ?: "")
            error = it

            if (error != null) animeAdapter.setError()
        }
    }

    private fun handleInitSearch() {
        if (!isInit) {
            SearchHelpers.handleInitSearch(requireView(), this)
            isInit = true
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    private fun setupAppBarListener(appBar: AppBarLayout) {
        appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
            isAppBarHidden = isCollapsed
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
        private const val INIT_SEARCH = "SEARCH"
    }
}