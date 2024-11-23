package com.kawaidev.kawaime.ui.fragments.streaming.episodes.helpers

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.appbar.AppBarLayout
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.streaming.EpisodesAdapter
import com.kawaidev.kawaime.utils.Converts

object EpisodesUtils {

    fun setupRecyclerView(
        recyclerView: RecyclerView,
        adapter: EpisodesAdapter,
        context: Context
    ) {
        recyclerView.apply {
            post {
                val spanCount = 5
                val space = Converts.dpToPx(4f, context).toInt()

                val gridLayoutManager = GridLayoutManager(context, spanCount)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                            EpisodesAdapter.LOADING_VIEW -> spanCount
                            EpisodesAdapter.ERROR_VIEW -> spanCount
                            else -> 1
                        }
                    }
                }

                layoutManager = gridLayoutManager

                if (itemDecorationCount == 0) {
                    addItemDecoration(
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

            this.adapter = adapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    fun setupAppBarListener(
        appBarLayout: AppBarLayout,
        onCollapseChange: (Boolean) -> Unit
    ) {
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) == appBarLayout.totalScrollRange
            onCollapseChange(isCollapsed)
        }
    }

    fun handleReverse(view: View, isReversed: Boolean, onReverse: (Boolean) -> Unit) {
        var reversed = isReversed

        val switchButton: ImageButton = view.findViewById(R.id.switch_button)
        switchButton.setOnClickListener {
            onReverse(!reversed)
            reversed = !reversed

            val drawableId = if (reversed) R.drawable.switch_right else R.drawable.switch_left
            switchButton.setImageResource(drawableId)
        }

        val drawableId = if (reversed) R.drawable.switch_right else R.drawable.switch_left
        switchButton.setImageResource(drawableId)
    }
}