package com.kawaidev.kawaime.ui.fragments.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeHelper
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
import com.kawaidev.kawaime.ui.listeners.FavoriteListener
import com.kawaidev.kawaime.ui.models.favorite.FavoriteViewModel
import com.kawaidev.kawaime.ui.models.favorite.FavoriteViewModelFactory
// import icepick.Icepick  // Temporarily disabled for hot reload

class FavoriteFragment : Fragment(), FavoriteListener {

    private lateinit var adapter: AnimeAdapter
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var service: AnimeService
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = AnimeAdapter(
            animeParams = AnimeParams(this, emptyList(), emptyMessage = "Opps, nothing is here! \n Your favorite added anime will appear here!"),
            onAgain = { getAnime() }
        )

        service = AnimeService.create()
        prefs = App.prefs
        prefs.setFavoriteListener(this)

        val factory = FavoriteViewModelFactory(service, prefs)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        setupUI(view)
        setupObservers()

        return view
    }

    private fun setupUI(view: View) {
        recycler = view.findViewById(R.id.recycler)

        recycler.apply {
            post {
                GridRecycler.setup(requireContext(), this@FavoriteFragment.adapter, recycler, viewModel.anime.value ?: emptyList())
            }
            adapter = this@FavoriteFragment.adapter
            addOnScrollListener(scrollListener())
        }

        refresh = view.findViewById(R.id.refresh)
        refresh.setColorSchemeResources(R.color.swipeColor, R.color.swipeColorAccent)

        refresh.setOnRefreshListener {
            getAnime(isRefresh = true)
        }

        if (viewModel.anime.value.isNullOrEmpty()) {
            getAnime()
        }
    }

    private fun setupObservers() {
        viewModel.anime.observe(viewLifecycleOwner) { updatedAnime ->
            AnimeHelper.updateGridData(adapter, updatedAnime, recycler)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) adapter.setLoading()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            refresh.isRefreshing = isRefreshing
        }

        viewModel.hasNextPage.observe(viewLifecycleOwner) { hasNextPage ->
            adapter.setNextPage(hasNextPage)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) adapter.setError()
        }
    }

    private fun scrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

            if (!viewModel.isLoading.value!! && viewModel.hasNextPage.value == true && lastVisibleItem >= totalItemCount - 5) {
                viewModel.getAnime()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Icepick.saveInstanceState(this, outState)
    }

    private fun getAnime(isRefresh: Boolean = false, isReload: Boolean = false) {
        viewModel.getAnime(isRefresh, isReload)
    }

    override fun onChange() {
        getAnime(isReload = true)
    }
}