package com.kawaidev.kawaime.ui.adapters.details

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.dao.anime.SearchResponse
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.AnimeAdapter
import com.kawaidev.kawaime.ui.custom.LinearSpacingItemDecoration
import com.kawaidev.kawaime.ui.custom.VerticalGradientImage
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import com.kawaidev.kawaime.ui.fragments.streaming.EpisodesFragment
import com.kawaidev.kawaime.utils.LoadImage
import java.util.Locale

class DetailsAdapter(
    private val fragment: Fragment,
    private var release: Release,
    private var isLoading: Boolean = false,
    private var isExpanded: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_DESCRIPTION = 2
        const val VIEW_TYPE_SEASONS = 3
        const val VIEW_TYPE_RELATED = 4
        const val VIEW_TYPE_RECOMMENDATIONS = 5
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) {
            VIEW_TYPE_LOADING
        } else {
            var currentPos = 0
            if (currentPos == position) return VIEW_TYPE_HEADER
            currentPos++

            if (release.anime?.info?.description.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_DESCRIPTION
                currentPos++
            }
            if (release.seasons.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_SEASONS
                currentPos++
            }
            if (release.relatedAnimes.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_RELATED
                currentPos++
            }
            if (release.recommendedAnimes.isNullOrEmpty().not()) {
                if (currentPos == position) return VIEW_TYPE_RECOMMENDATIONS
            }
            throw IllegalArgumentException("Invalid view type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.loading_view, parent, false))
            VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
            VIEW_TYPE_DESCRIPTION -> DescriptionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_description, parent, false))
            VIEW_TYPE_SEASONS -> SeasonsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            VIEW_TYPE_RELATED -> RelatedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            VIEW_TYPE_RECOMMENDATIONS -> RecommendationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anime_view, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_LOADING -> (holder as LoadingViewHolder).bind()
            VIEW_TYPE_HEADER -> (holder as HeaderViewHolder).bind(release)
            VIEW_TYPE_DESCRIPTION -> (holder as DescriptionViewHolder).bind(release.anime?.info?.description ?: "")
            VIEW_TYPE_SEASONS -> release.seasons?.let { (holder as SeasonsViewHolder).bind(it) }
            VIEW_TYPE_RELATED -> release.relatedAnimes?.let { (holder as RelatedViewHolder).bind(it) }
            VIEW_TYPE_RECOMMENDATIONS -> release.recommendedAnimes?.let {
                (holder as RecommendationsViewHolder).bind(
                    it
                )
            }
        }
    }

    override fun getItemCount(): Int {
        if (isLoading) return 1

        var count = 1
        if (release.anime?.info?.description.isNullOrEmpty().not()) count++
        if (release.seasons.isNullOrEmpty().not()) count++
        if (release.relatedAnimes.isNullOrEmpty().not()) count++
        if (release.recommendedAnimes.isNullOrEmpty().not()) count++

        return count
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {

        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(release: Release) {
            val image = itemView.findViewById<VerticalGradientImage>(R.id.image)
            LoadImage().loadImage(itemView.context, release.anime?.info?.poster, image, originalSize = true)

            itemView.findViewById<TextView>(R.id.title).apply {
                text = release.anime?.info?.name
                setOnClickListener {
                    (fragment.requireActivity() as MainActivity).bottomSheets.onTitleSheet(release)
                }
            }
            itemView.findViewById<TextView>(R.id.type).text = release.anime?.info?.stats?.type
            itemView.findViewById<TextView>(R.id.rating).text = release.anime?.info?.stats?.rating
            itemView.findViewById<TextView>(R.id.season).text = release.anime?.moreInfo?.premiered
            itemView.findViewById<TextView>(R.id.status).text = release.anime?.moreInfo?.status
            itemView.findViewById<TextView>(R.id.duration).text = release.anime?.moreInfo?.duration
            itemView.findViewById<TextView>(R.id.score).text = release.anime?.moreInfo?.malscore

            val textGenres: TextView = itemView.findViewById(R.id.genres)
            val genresText = SpannableStringBuilder()
            release.anime?.moreInfo?.genres?.forEachIndexed { index, genre ->
                if (index != 0) genresText.append(" • ")
                val start = genresText.length

                val formattedGenre = genre.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
                genresText.append(formattedGenre)

                genresText.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val bundle = Bundle().apply {
                            putString("search", formattedGenre)
                        }
                        val searchFragment = SearchFragment().apply {
                            arguments = bundle
                        }

                        (fragment.requireActivity() as MainActivity).pushFragment(searchFragment)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(fragment.requireContext(), R.color.text5)
                    }
                }, start, genresText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textGenres.text = genresText
            textGenres.movementMethod = LinkMovementMethod.getInstance()

            itemView.findViewById<FrameLayout>(R.id.watch_button).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", release.anime?.info?.id)
                    putString("title", release.anime?.info?.name)
                }
                val episodesFragment = EpisodesFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(episodesFragment)
            }
        }
    }

    inner class DescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(description: String) {

            val textDescription: TextView = itemView.findViewById(R.id.textDescription)
            val detailsButton: FrameLayout = itemView.findViewById(R.id.detailsButton)
            val detailsButtonText: TextView = itemView.findViewById(R.id.detailsButtonText)

            textDescription.text = description
            textDescription.maxLines = if (isExpanded) Integer.MAX_VALUE else 5
            detailsButtonText.text = if (isExpanded) "Hide" else "Details..."

            textDescription.post {
                textDescription.layout?.let { layout ->
                    detailsButton.visibility = if (layout.lineCount > 5) View.VISIBLE else View.GONE
                }
            }

            detailsButton.setOnClickListener {
                isExpanded = !isExpanded

                val startHeight = textDescription.height
                textDescription.maxLines = if (isExpanded) Integer.MAX_VALUE else 5

                textDescription.measure(
                    View.MeasureSpec.makeMeasureSpec(textDescription.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                val endHeight = textDescription.measuredHeight

                val valueAnimator = ValueAnimator.ofInt(startHeight, endHeight)
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    textDescription.layoutParams.height = value
                    textDescription.requestLayout()
                }

                detailsButtonText.text = if (isExpanded) "Hide" else "Details..."
                valueAnimator.duration = 250
                valueAnimator.start()
            }
        }
    }

    inner class SeasonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(seasonItems: List<Season>) {
            val seasons = seasonItems.filter { it.isCurrent == false }

            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = "Other Seasons"

            val latestAdapter = SeasonAdapter(fragment, seasons)
            val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recycler.adapter = latestAdapter

            if (recycler.itemDecorationCount == 0) {
                recycler.addItemDecoration(LinearSpacingItemDecoration(8, true))
            }
        }
    }

    inner class RelatedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(relatedItems: List<SearchResponse>) {
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = "Related"

            val latestAdapter = AnimeAdapter(fragment, relatedItems)
            val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recycler.adapter = latestAdapter

            val snapHelper = LinearSnapHelper()

            if (recycler.onFlingListener == null) {
                snapHelper.attachToRecyclerView(recycler)
            }

            if (recycler.itemDecorationCount > 0) {
                recycler.removeItemDecorationAt(0)
            }

            recycler.addItemDecoration(LinearSpacingItemDecoration(8, true))
        }
    }

    inner class RecommendationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recommendationItems: List<SearchResponse>) {
            val titleTextView: TextView = itemView.findViewById(R.id.title)
            titleTextView.text = "Recommendations"

            val latestAdapter = AnimeAdapter(fragment, recommendationItems)
            val recycler = itemView.findViewById<RecyclerView>(R.id.recycler)
            recycler.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recycler.adapter = latestAdapter

            val snapHelper = LinearSnapHelper()

            if (recycler.onFlingListener == null) {
                snapHelper.attachToRecyclerView(recycler)
            }

            if (recycler.itemDecorationCount == 0) {
                recycler.addItemDecoration(LinearSpacingItemDecoration(8, true))
            }
        }
    }

    fun setData(release: Release) {
        this@DetailsAdapter.release = release
        notifyDataSetChanged()
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataSetChanged()
    }
}
