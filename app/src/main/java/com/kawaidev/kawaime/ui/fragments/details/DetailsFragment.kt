package com.kawaidev.kawaime.ui.fragments.details

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kawaidev.kawaime.App
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.details.DetailsAdapter
import com.kawaidev.kawaime.ui.listeners.FavoriteListener
import com.kawaidev.kawaime.utils.LoadImage
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch

class DetailsFragment : Fragment(), FavoriteListener {
    private lateinit var service: AnimeService
    private lateinit var adapter: DetailsAdapter
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var image: ImageView
    lateinit var prefs: Prefs

    private var id: String = ""

    @State private var release: Release = Release()
    @State private var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           id = it.getString(ID) ?: ""
        }

        Icepick.restoreInstanceState(this, savedInstanceState)

        service = AnimeService.create()
        adapter = DetailsAdapter(this, release)
        prefs = App.prefs
        prefs.setFavoriteListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        image = view.findViewById(R.id.mainImage)

        val back: ImageButton = view.findViewById(R.id.button_back)

        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        val manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recycler.apply {
            recycler.layoutManager = manager
            recycler.adapter = this@DetailsFragment.adapter
        }

        if (!isFetched) {
            getAnime {
                if (isLandscape) LoadImage().loadImage(requireContext(), release.anime?.info?.poster, image)
            }
        } else {
            if (isLandscape) LoadImage().loadImage(requireContext(), release.anime?.info?.poster, image)
        }

        refresh = view.findViewById(R.id.refresh)

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            getAnime(true) {
                if (isLandscape) LoadImage().loadImage(requireContext(), release.anime?.info?.poster, image)
            }
        }

        return view
    }

    fun getAnime(refresh: Boolean = false, onComplete: () -> Unit) {
        if (refresh) this@DetailsFragment.refresh.isRefreshing = true
        else {
            adapter.setLoading(true)
            image.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val release = service.getAnime(id)

                adapter.setData(release)
                this@DetailsFragment.release = release
                if (!refresh) image.visibility = View.VISIBLE
            } catch (e: Exception) {

            } finally {
                if (refresh) this@DetailsFragment.refresh.isRefreshing = false
                else adapter.setLoading(false)
                isFetched = true
                onComplete()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    companion object {
        const val ID = "id"
    }

    override fun onChange() {
        adapter.notifyDataSetChanged()
    }
}