package com.kawaidev.kawaime.ui.fragments.filter

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.decoration.GridDecoration
import com.kawaidev.kawaime.ui.adapters.filter.FilterAdapter
import com.kawaidev.kawaime.ui.adapters.filter.helpers.FilterEnum
import com.kawaidev.kawaime.ui.adapters.filter.helpers.FilterItem
import com.kawaidev.kawaime.ui.adapters.filter.helpers.FilterType
import com.kawaidev.kawaime.ui.fragments.result.ResultFragment
import com.kawaidev.kawaime.utils.Converts
import icepick.Icepick
import icepick.State
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FilterFragment : Fragment() {
    private lateinit var adapter: FilterAdapter

    @State private var filters: MutableList<FilterItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            filters = getDefaultFilters().toMutableList()
        }

        adapter = FilterAdapter(this, filters)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)

        val back: ImageButton = view.findViewById(R.id.back_button)
        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        recycler.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (filters[position].pairedWith != null) 1 else 2
            }
        }
        recycler.layoutManager = layoutManager

        val space = Converts.dpToPx(16f, requireContext()).toInt()
        val spaceVer = Converts.dpToPx(20f, requireContext()).toInt()
        recycler.addItemDecoration(SpacingItemDecoration(
            Spacing(space, spaceVer, Rect(space, space, space, space))
        ))

        val reset: Button = view.findViewById(R.id.reset_button)
        val apply: Button = view.findViewById(R.id.apply_button)

        reset.setOnClickListener {
            adapter.reset()
        }

        apply.setOnClickListener {
            val bundle = Bundle().apply {
                putString("search_params", Json.encodeToString(adapter.getParams()))
            }
            val resultFragment = ResultFragment().apply { arguments = bundle }
            (requireActivity() as MainActivity).pushFragment(resultFragment)
        }

        return view
    }

    private fun getDefaultFilters(): List<FilterItem> {
        return listOf(
            FilterItem(FilterEnum.TYPE, FilterType.DEFAULT),
            FilterItem(FilterEnum.GENRES, FilterType.CUSTOM, desc = "Will search anime with the given genres. \nRecommended to enter no more than 3 genres.") {
                (activity as? MainActivity)?.dialogs?.onGenres(it)
            },
            FilterItem(FilterEnum.RATED, FilterType.DEFAULT, desc = "Filters anime by age rating."),
            FilterItem(FilterEnum.SEASON, FilterType.DEFAULT, pairedWith = FilterEnum.SCORE),
            FilterItem(FilterEnum.SCORE, FilterType.DEFAULT, pairedWith = FilterEnum.SEASON),
            FilterItem(FilterEnum.STATUS, FilterType.DEFAULT),
            FilterItem(FilterEnum.DATE, FilterType.CUSTOM) {
                (activity as? MainActivity)?.dialogs?.onYear(it)
            },
            FilterItem(FilterEnum.LANGUAGE, FilterType.DEFAULT),
            FilterItem(FilterEnum.SORT, FilterType.DEFAULT, defaultValue = "Default", desc = "Sort the results based on specific criteria. \nYou can choose 'Default' or other sorting options.")
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
        outState.putParcelableArrayList("filters", ArrayList(filters))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState)
            filters = savedInstanceState.getParcelableArrayList("filters") ?: mutableListOf()
            adapter.notifyDataSetChanged()
        }
    }

    fun saveFilters(filtersItem: MutableList<FilterItem>) {
        filters = filtersItem
    }
}