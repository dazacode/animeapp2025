package com.kawaidev.kawaime.ui.adapters.details.helpers

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.network.routes.AnimeRoutes
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.decoration.LinearDecoration
import com.kawaidev.kawaime.ui.adapters.details.CardAdapter
import com.kawaidev.kawaime.ui.adapters.details.TextAdapter
import com.kawaidev.kawaime.ui.custom.VerticalGradientImage
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment
import com.kawaidev.kawaime.utils.Converts
import com.kawaidev.kawaime.utils.LoadImage
import java.util.Locale

object DetailsHelper {
    @OptIn(UnstableApi::class)
    fun headerBind(fragment: DetailsFragment, itemView: View, release: Release) {
        setupImage(itemView, release)
        setupTitle(fragment, itemView, release)
        setupInfoTextViews(itemView, release)
        setupGenres(fragment, itemView, release)
        setupWatchButton(fragment, itemView, release)
        setupShareButton(itemView, release)
        setupFavoriteButton(fragment, itemView, release)
    }

    private fun setupImage(itemView: View, release: Release) {
        val image = itemView.findViewById<VerticalGradientImage>(R.id.image)
        LoadImage().loadImage(
            itemView.context,
            release.shikimori?.poster?.originalUrl ?: release.anime?.info?.poster,
            image
        )
    }

    private fun setupTitle(fragment: DetailsFragment, itemView: View, release: Release) {
        val titleTextView = itemView.findViewById<TextView>(R.id.title)
        titleTextView.text = release.anime?.info?.name
        titleTextView.setOnClickListener {
            (fragment.requireActivity() as MainActivity).bottomSheets.onTitleSheet(release)
        }
    }

    private fun setupInfoTextViews(itemView: View, release: Release) {
        val cardRecycler = itemView.findViewById<RecyclerView>(R.id.cardsRecycler)
        val detailsRecycler = itemView.findViewById<RecyclerView>(R.id.detailsRecycler)

        val adultRatings = listOf("R", "R+", "Rx", "18+")
        val cardItems = mutableListOf<CardItem>()

        release.anime?.info?.stats?.type?.let {
            cardItems.add(CardItem(it, R.color.statusCard, R.color.text))
        }

        if (release.anime?.info?.stats?.rating in adultRatings)
            cardItems.add(CardItem("18+", R.color.red, R.color.white))

        release.anime?.info?.stats?.rating?.let {
            cardItems.add(CardItem(it, R.color.statusCard, R.color.text))
        }

        cardRecycler.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        cardRecycler.adapter = CardAdapter(cardItems)

        val textItems = listOf(
            TextItem(release.anime?.moreInfo?.premiered),
            TextItem(release.anime?.moreInfo?.status),
            TextItem(release.anime?.info?.stats?.duration),
            TextItem(
                (release.shikimori?.score ?: release.anime?.moreInfo?.malscore).toString(),
                R.drawable.star,
                Converts.dpToPx(13f, itemView.context).toInt()
            )
        ).filter { (it.text == null || it.text == "?m" || it.text == "?").not() }

        detailsRecycler.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        detailsRecycler.adapter = TextAdapter(textItems)

        val padding = Converts.dpToPx(8f, itemView.context).toInt()

        detailsRecycler.apply {
            while (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }

            addItemDecoration(LinearDecoration(horizontal = padding))
        }

        cardRecycler.apply {
            while (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }

            addItemDecoration(LinearDecoration(horizontal = padding))
        }
    }

    private fun setupGenres(fragment: DetailsFragment, itemView: View, release: Release) {
        val textGenres = itemView.findViewById<TextView>(R.id.genres)
        val genresText = SpannableStringBuilder()
        release.anime?.moreInfo?.genres?.forEachIndexed { index, genre ->
            if (index != 0) genresText.append(" • ")
            val formattedGenre = genre.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
            val start = genresText.length

            genresText.append(formattedGenre)
            genresText.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val bundle = Bundle().apply { putString("SEARCH", formattedGenre) }
                    val searchFragment = SearchFragment().apply { arguments = bundle }
                    (fragment.requireActivity() as MainActivity).pushFragment(searchFragment)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(fragment.requireContext(), R.color.text5)
                }
            }, start, genresText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textGenres.text = genresText
        textGenres.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupWatchButton(fragment: DetailsFragment, itemView: View, release: Release) {
        val watchButton = itemView.findViewById<MaterialButton>(R.id.watch_button)
        val animeId = release.anime?.info?.id ?: ""

        val isNotYetAired = release.anime?.moreInfo?.status == "Not yet aired" && true == false
        watchButton.text = if (isNotYetAired) {
            itemView.context.getString(R.string.unavailable)
        } else {
            itemView.context.getString(R.string.watch)
        }
        watchButton.isEnabled = !isNotYetAired

        watchButton.icon = ContextCompat.getDrawable(
            itemView.context,
            if (isNotYetAired) R.drawable.block else R.drawable.play
        )

        watchButton.iconSize = if (isNotYetAired) {
            itemView.context.resources.getDimensionPixelSize(R.dimen.icon_size_small)
        } else {
            itemView.context.resources.getDimensionPixelSize(R.dimen.icon_size_large)
        }

        watchButton.backgroundTintList = ContextCompat.getColorStateList(
            itemView.context,
            if (isNotYetAired) R.color.mainDisabled else R.color.main
        )

        watchButton.backgroundTintMode = PorterDuff.Mode.SRC_IN

        watchButton.setOnClickListener {
            if (!isNotYetAired) {
                val bundle = Bundle().apply {
                    putString("id", animeId)
                    putString("title", release.anime?.info?.name)
                }
                val episodesFragment = EpisodesFragment().apply { arguments = bundle }
                (fragment.requireActivity() as MainActivity).pushFragment(episodesFragment)
            }
        }
    }

    private fun setupShareButton(itemView: View, release: Release) {
        val shareButton = itemView.findViewById<ImageButton>(R.id.shareButton)

        shareButton.setOnClickListener {
            share(itemView.context, "${AnimeRoutes.BASE_JAVA}details/${release.anime?.info?.id}")
        }
    }

    private fun share(context: Context, link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Link via"))
    }


    private fun setupFavoriteButton(fragment: DetailsFragment, itemView: View, release: Release) {
        val animeId = release.anime?.info?.id ?: ""
        var isFavorite = fragment.prefs.isFavorite(animeId)
        val favoriteButton = itemView.findViewById<ImageButton>(R.id.favoriteButton)
        updateFavoriteIcon(isFavorite, favoriteButton, fragment)

        favoriteButton.setOnClickListener {
            isFavorite = if (isFavorite) {
                fragment.prefs.removeFavorite(animeId)
                (fragment.requireActivity() as MainActivity).showSnackbar(itemView.context.getString(R.string.removed_from_favorites))
                false
            } else {
                fragment.prefs.addFavorite(animeId)
                (fragment.requireActivity() as MainActivity).showSnackbar(itemView.context.getString(R.string.added_to_favorites))
                true
            }
            updateFavoriteIcon(isFavorite, favoriteButton, fragment)
        }
    }

    fun updateFavoriteIcon(isFavorite: Boolean, favoriteButton: ImageButton, fragment: DetailsFragment) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.favorite)
            favoriteButton.setColorFilter(ContextCompat.getColor(fragment.requireContext(), R.color.red))
        } else {
            favoriteButton.setImageResource(R.drawable.favorite_outlined)
            favoriteButton.setColorFilter(ContextCompat.getColor(fragment.requireContext(), R.color.icon))
        }
    }

    fun descBind(description: String, itemView: View, isExpanded: Boolean, onExpanded: ((expanded: Boolean) -> Unit)) {
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val detailsButton: Button = itemView.findViewById(R.id.detailsButton)

        var expanded = isExpanded

        val details = itemView.context.getString(R.string.details___)
        val hide = itemView.context.getString(R.string.hide)

        textDescription.text = description
        textDescription.maxLines = if (expanded) Integer.MAX_VALUE else 5
        detailsButton.text = if (expanded) hide else details

        textDescription.post {
            textDescription.layout?.let { layout ->
                detailsButton.visibility = if (layout.lineCount > 5) View.VISIBLE else View.GONE
            }
        }

        detailsButton.setOnClickListener {
            expanded = !expanded

            val startHeight = textDescription.height
            textDescription.maxLines = if (expanded) Integer.MAX_VALUE else 5

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

            detailsButton.text = if (expanded) hide else details
            valueAnimator.duration = 250
            valueAnimator.start()
        }
    }
}