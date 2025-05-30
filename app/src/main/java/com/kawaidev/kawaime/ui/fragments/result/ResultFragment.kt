package com.kawaidev.kawaime.ui.fragments.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.api_utils.SearchParams
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeHelper
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ResultFragment : BaseAnimeFragment() {
    private var searchParams: SearchParams = SearchParams()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchParams = Json.decodeFromString(it.getString(SEARCH_PARAMS) ?: "")
        }
        adapter = AnimeAdapter(
            animeParams = AnimeParams(this, anime),
            onAgain = { fetchAnimeData(1) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val edit: Button = view!!.findViewById(R.id.edit_button)
        edit.setOnClickListener { (activity as? MainActivity)?.popFragment() }

        if (!isFetched) fetchAnimeData(page)
        return view
    }

    override fun fetchAnimeData(page: Int) {
        isLoading = true
        adapter.setLoading()

        this.page = page
        searchParams.page = page

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wrapper = service.searchAnime(searchParams)
                hasNextPage = wrapper.hasNextPage
                anime += wrapper.animes
                AnimeHelper.updateGridData(adapter, anime, recycler)
                adapter.setNextPage(hasNextPage)
            } catch (e: Exception) {
                adapter.setError()
            } finally {
                isLoading = false
                isFetched = true
            }
        }
    }

    companion object {
        const val SEARCH_PARAMS = "search_params"
    }
}