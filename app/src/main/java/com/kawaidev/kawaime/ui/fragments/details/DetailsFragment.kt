package com.kawaidev.kawaime.ui.fragments.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.details.DetailsAdapter
import com.kawaidev.kawaime.ui.fragments.details.helpers.DetailsHelper
import com.kawaidev.kawaime.ui.listeners.FavoriteListener
import com.kawaidev.kawaime.ui.models.details.DetailsViewModel
import com.kawaidev.kawaime.utils.LoadImage
import icepick.Icepick
import icepick.State

class DetailsFragment : Fragment(), FavoriteListener {
    private lateinit var adapter: DetailsAdapter
    private lateinit var refresh: SwipeRefreshLayout
    lateinit var viewModel: DetailsViewModel
    lateinit var image: ImageView
    lateinit var prefs: Prefs

    var id: String = ""

    private var isLandscape: Boolean = false

    @State private var release: Release = Release()
    @State private var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           id = it.getString(ID) ?: ""
        }

        Icepick.restoreInstanceState(this, savedInstanceState)

        adapter = DetailsAdapter(this, release)
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        prefs = App.prefs
        prefs.setFavoriteListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        isLandscape = resources.configuration.screenWidthDp >= 600

        image = view.findViewById(R.id.mainImage)

        val back: ImageButton = view.findViewById(R.id.button_back)

        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        val manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recycler.apply {
            layoutManager = manager
            adapter = this@DetailsFragment.adapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        refresh = view.findViewById(R.id.refresh)

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            viewModel.fetchAnime(id, true)
        }

        handleObservers()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFetched) {
            viewModel.fetchAnime(id)
        } else {
            if (isLandscape) LoadImage().loadImage(requireContext(), viewModel.release.value?.shikimori?.poster?.originalUrl ?: viewModel.release.value?.anime?.info?.poster, image)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    private fun handleObservers() {
        viewModel.release.observe(viewLifecycleOwner) { release ->
            adapter.setLoading(false)
            adapter.setError(false)
            adapter.setData(release)
            this@DetailsFragment.release = release
            isFetched = true

            if (isLandscape) {
                LoadImage().loadImage(requireContext(), release.shikimori?.poster?.originalUrl ?: release.anime?.info?.poster, image)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            adapter.setError(error.isNullOrEmpty().not())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            adapter.setLoading(loading)

            if (isLandscape) {
                if (loading) {
                    DetailsHelper.hideImage(this)
                } else if (viewModel.error.value.isNullOrEmpty()) {
                    DetailsHelper.showImage(this)
                }
            }
        }

        viewModel.isRefresh.observe(viewLifecycleOwner) { refresh ->
            this@DetailsFragment.refresh.isRefreshing = refresh
        }
    }

    companion object {
        const val ID = "id"
    }

    override fun onChange() {
        adapter.notifyItemChanged(0)
    }
}