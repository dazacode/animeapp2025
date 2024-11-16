package com.kawaidev.kawaime.ui.bottomSheets

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.streaming.EpisodeServers
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.details.ServerAdapter
import com.kawaidev.kawaime.ui.adapters.streaming.EpisodesAdapter
import com.kawaidev.kawaime.ui.fragments.streaming.EpisodesFragment
import com.kawaidev.kawaime.utils.Converts

class BottomSheets(private val activity: MainActivity) {
    fun onBottomSheet(episodeServers: EpisodeServers, fragment: EpisodesFragment) {
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
        val serverAdapter = ServerAdapter(fragment, episodeServers)

        recycler.apply {
            post {
                val spanCount = 2
                val space = Converts.dpToPx(4f, fragment.requireContext()).toInt()


                val gridLayoutManager = GridLayoutManager(context, spanCount)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (serverAdapter.getItemViewType(position)) {
                            ServerAdapter.HEADER_VIEW -> spanCount
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

    @SuppressLint("SetTextI18n")
    fun onTitleSheet(release: Release) {
        val view = activity.layoutInflater.inflate(R.layout.bottom_sheet_layout_title, null)

        // English Title
        val titleTextView = view.findViewById<TextView>(R.id.title)
        val titleContainer = view.findViewById<LinearLayout>(R.id.titleContainer)
        setTitleVisibility(titleContainer, titleTextView, release.anime?.info?.name)

        // Original (Romaji) Title
        val originalTitleTextView = view.findViewById<TextView>(R.id.originalTitle)
        val origTitleContainer = view.findViewById<LinearLayout>(R.id.origTitleContainer)
        setTitleVisibility(origTitleContainer, originalTitleTextView, release.anime?.moreInfo?.japanese)

        // Other Synonym Titles
        val otherTitleTextView = view.findViewById<TextView>(R.id.otherTitle)
        val otherTitleContainer = view.findViewById<LinearLayout>(R.id.otherTitleContainer)
        setTitleVisibility(otherTitleContainer, otherTitleTextView, release.anime?.moreInfo?.synonyms?.split(", ")?.joinToString(separator = "\n"))

        // Bottom sheet setup
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

        // Close button
        view.findViewById<FrameLayout>(R.id.closeButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Copy buttons
        view.findViewById<ImageButton>(R.id.copyTitle).setOnClickListener {
            activity.copyToClipboard("Title", titleTextView.text.toString())
        }

        view.findViewById<ImageButton>(R.id.copyOrigTitle).setOnClickListener {
            activity.copyToClipboard("Original Title", originalTitleTextView.text.toString())
        }

        view.findViewById<ImageButton>(R.id.copyOtherTitle).setOnClickListener {
            activity.copyToClipboard("Other Titles", otherTitleTextView.text.toString())
        }
    }

    private fun setTitleVisibility(container: LinearLayout, textView: TextView, titleText: String?) {
        if (titleText.isNullOrEmpty()) {
            container.visibility = View.GONE
        } else {
            container.visibility = View.VISIBLE
            textView.text = titleText
        }
    }
}