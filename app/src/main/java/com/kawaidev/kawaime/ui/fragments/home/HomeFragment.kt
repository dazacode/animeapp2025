package com.kawaidev.kawaime.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Home
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.adapters.home.HomeAdapter
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdapter
    private lateinit var service: AnimeService

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    @State private var home: Home = Home()
    @State private var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Icepick.restoreInstanceState(this, savedInstanceState)

        adapter = HomeAdapter(this, home)
        service = AnimeService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recycler)

        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recycler.apply {
            recycler.layoutManager = this@HomeFragment.layoutManager
            recycler.adapter = this@HomeFragment.adapter
        }

        if (!isFetched) {
            getHome()
        }

        refresh = view.findViewById(R.id.refresh)

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            getHome(true)
        }
    }

    private fun getHome(isRefresh: Boolean = false) {
        when(isRefresh) {
            true -> refresh.isRefreshing = true
            false -> adapter.setLoading(true)
        }
        adapter.setError(false)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val home = service.getHome()

                adapter.setHome(home)
                when(isRefresh) {
                    true -> refresh.isRefreshing = false
                    false -> adapter.setLoading(false)
                }
            } catch (e: Exception) {
                when(isRefresh) {
                    true -> refresh.isRefreshing = false
                    false -> adapter.setLoading(false)
                }
                adapter.setError(true)
            } finally {
                isFetched = true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState);
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}