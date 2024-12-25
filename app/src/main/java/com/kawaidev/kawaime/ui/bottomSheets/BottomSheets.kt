package com.kawaidev.kawaime.ui.bottomSheets

import android.graphics.Rect
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.network.dao.streaming.Episodes
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.bottom.TitlesAdapter
import com.kawaidev.kawaime.ui.adapters.decoration.DividerDecoration
import com.kawaidev.kawaime.ui.adapters.streaming.ServerAdapter
import com.kawaidev.kawaime.ui.bottomSheets.helpers.TitleItem
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment
import com.kawaidev.kawaime.utils.Converts

class BottomSheets(private val activity: MainActivity) {
    fun onBottomSheet(episodeServers: EpisodeServers, episodes: Episodes, fragment: EpisodesFragment) {
        val view = activity.layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        val bottomSheetDialog = BottomSheetDialog(activity, R.style.CustomSheetStyle)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.show()

        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.peekHeight = 0

        val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams

        val recycler: RecyclerView = view.findViewById(R.id.recycler)
        val serverAdapter = ServerAdapter(fragment, episodeServers, episodes)

        recycler.apply {
            post {
                val spanCount = 2
                val space = Converts.dpToPx(4f, fragment.requireContext()).toInt()


                val gridLayoutManager = GridLayoutManager(context, spanCount)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (serverAdapter.getItemViewType(position)) {
                            ServerAdapter.HEADER_VIEW -> spanCount
                            ServerAdapter.DOWNLOAD_VIEW -> spanCount
                            else -> 1
                        }
                    }
                }

                layoutManager = gridLayoutManager

                if (recycler.itemDecorationCount == 0) {
                    recycler.addItemDecoration(
                        SpacingItemDecoration(
                            Spacing(
                                horizontal = space,
                                vertical = space,
                                edges = Rect(space, space, space, space)
                            )
                        )
                    )
                }
            }

            adapter = serverAdapter

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        view.findViewById<FrameLayout>(R.id.closeButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        serverAdapter.setOnServerSelectedListener(object : ServerAdapter.OnServerSelectedListener {
            override fun onServerSelected() {
                bottomSheetDialog.dismiss()
            }
        })
    }

    fun onTitleSheet(release: Release) {
        val view = activity.layoutInflater.inflate(R.layout.bottom_sheet_layout_title, null)
        val recycler: RecyclerView = view.findViewById(R.id.recycler)

        val titles = listOf(
            TitleItem(activity.getString(R.string.title), release.anime?.info?.name),
            TitleItem(activity.getString(R.string.orig_title), release.anime?.moreInfo?.japanese),
            TitleItem(
                activity.getString(R.string.other_titles),
                release.anime?.moreInfo?.synonyms
                    ?.split(Regex(", (?=[A-Z0-9])"))
                    ?.joinToString("\n")
            )
        ).filterNot { it.content.isNullOrEmpty() }

        val adapter = TitlesAdapter(titles) { content ->
            activity.copyToClipboard("Copied Title", content)
        }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity)

        recycler.addItemDecoration(DividerDecoration(
            context = activity,
            size = activity.resources.getDimensionPixelSize(R.dimen.dividerHeight),
            color = activity.getColor(R.color.dividerColor),
            includeLast = false
        ))

        val bottomSheetDialog = BottomSheetDialog(activity, R.style.CustomSheetStyle)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        view.findViewById<FrameLayout>(R.id.closeButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.peekHeight = 0
    }
}