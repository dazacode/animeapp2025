package com.kawaidev.kawaime.ui.fragments.explore.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.schedule.ScheduleViewPagerAdapter
import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        val back: ImageButton = view.findViewById(R.id.back_button)
        back.setOnClickListener {
            (activity as? MainActivity)?.popFragment()
        }

        setupViewPagerAndTabs()

        return view
    }

    private fun setupViewPagerAndTabs() {
        viewPager.adapter = ScheduleViewPagerAdapter(this)
        viewPager.offscreenPageLimit = 7

        val weekdays = arrayOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )

        val todayPosition = getCurrentDayPosition()

        // Set default tab without animation
        viewPager.setCurrentItem(todayPosition, false)

        // Attach TabLayoutMediator AFTER setting the current item
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = weekdays[position]
        }.attach()
    }

    private fun getCurrentDayPosition(): Int {
        val today = LocalDate.now().dayOfWeek
        return when (today) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
            else -> 0
        }
    }

    companion object {

    }
}