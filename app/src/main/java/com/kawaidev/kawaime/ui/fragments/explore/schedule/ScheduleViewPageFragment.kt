package com.kawaidev.kawaime.ui.fragments.explore.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Schedule
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.adapters.schedule.ScheduleAdapter
import icepick.Icepick
import icepick.State
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ScheduleViewPageFragment : Fragment() {
    private lateinit var service: AnimeService
    private lateinit var adapter: ScheduleAdapter

    private lateinit var refresh: SwipeRefreshLayout

    private var position: Int = 0

    @State private var schedule: List<Schedule> = emptyList()
    @State private var isFetched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Icepick.restoreInstanceState(this, savedInstanceState)

        arguments?.let {
            position = it.getInt(POSITION)
        }

        service = AnimeService.create()
        adapter = ScheduleAdapter(this, schedule) {
            loadSchedule()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_view_page, container, false)

        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = this@ScheduleViewPageFragment.adapter

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            DividerItemDecoration.VERTICAL
        )
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        if (dividerDrawable != null) {
            dividerItemDecoration.setDrawable(dividerDrawable)
        }
        recycler.addItemDecoration(dividerItemDecoration)

        refresh = view.findViewById(R.id.refresh)

        refresh.setColorSchemeResources(
            R.color.swipeColor,
            R.color.swipeColorAccent,
        )

        refresh.setOnRefreshListener {
            loadSchedule(true)
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        if (!isFetched) {
            loadSchedule()
        }
    }

    private fun loadSchedule(isRefresh: Boolean = false) {
        if (!isRefresh) adapter.setLoading() else refresh.isRefreshing = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val schedule = service.getSchedule(getDateForPosition())

                adapter.setItems(schedule)
            } catch (e: Exception) {
                adapter.setError()
            } finally {
                if (isRefresh) refresh.isRefreshing = false
                isFetched = true
            }
        }
    }

    private fun getDateForPosition(): LocalDate {
        val today = LocalDate.now()

        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        return startOfWeek.plusDays(position.toLong())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    companion object {
        const val POSITION = "position"

        @JvmStatic
        fun newInstance(position: Int) =
            ScheduleViewPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(POSITION, position)
                }

                retainInstance = true
            }
    }
}