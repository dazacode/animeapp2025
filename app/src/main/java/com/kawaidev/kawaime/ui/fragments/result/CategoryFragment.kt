package com.kawaidev.kawaime.ui.fragments.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeHelper
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeParams
import kotlinx.coroutines.launch

class CategoryFragment : BaseAnimeFragment() {
    private var category: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(CATEGORY) ?: ""
            title = it.getString(TITLE) ?: ""
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

        view!!.findViewById<TextView>(R.id.title).text = title
        val edit: Button = view.findViewById(R.id.edit_button)
        edit.visibility = View.GONE

        if (!isFetched) fetchAnimeData(page)
        return view
    }

    override fun fetchAnimeData(page: Int) {
        isLoading = true
        adapter.setLoading()

        this.page = page

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (animeList, hasNextPage) = service.getCategory(category, page)
                AnimeHelper.updateGridData(adapter, anime + animeList, recycler)
                adapter.setNextPage(hasNextPage)
                this@CategoryFragment.hasNextPage = hasNextPage
                anime += animeList
            } catch (e: Exception) {
                adapter.setError()
            } finally {
                isLoading = false
                isFetched = true
            }
        }
    }

    companion object {
        const val CATEGORY = "CATEGORY"
        const val TITLE = "title"
    }
}