package com.kawaidev.kawaime.ui.adapters.schedule

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kawaidev.kawaime.ui.fragments.explore.schedule.ScheduleViewPageFragment

class ScheduleViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment = ScheduleViewPageFragment.newInstance(position)
}