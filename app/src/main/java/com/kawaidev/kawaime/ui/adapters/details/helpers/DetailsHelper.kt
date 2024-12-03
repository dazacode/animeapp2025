package com.kawaidev.kawaime.ui.adapters.details.helpers

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Release
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.custom.VerticalGradientImage
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import com.kawaidev.kawaime.ui.fragments.streaming.episodes.EpisodesFragment
import com.kawaidev.kawaime.utils.LoadImage
import java.util.Locale

object DetailsHelper {
    fun headerBind(fragment: DetailsFragment, itemView: View, release: Release) {
        val image = itemView.findViewById<VerticalGradientImage>(R.id.image)
        val rating = release.anime?.info?.stats?.rating
        val animeId = release.anime?.info?.id ?: ""

        LoadImage().loadImage(itemView.context, release.anime?.info?.poster, image)

        itemView.findViewById<TextView>(R.id.title).apply {
            text = release.anime?.info?.name
            setOnClickListener {
                (fragment.requireActivity() as MainActivity).bottomSheets.onTitleSheet(release)
            }
        }

        itemView.findViewById<TextView>(R.id.type).text = release.anime?.info?.stats?.type
        itemView.findViewById<TextView>(R.id.rating).text = rating
        itemView.findViewById<TextView>(R.id.season).text = release.anime?.moreInfo?.premiered
        itemView.findViewById<TextView>(R.id.status).text = release.anime?.moreInfo?.status
        itemView.findViewById<TextView>(R.id.duration).apply {
            text = release.anime?.moreInfo?.duration
            if (release.anime?.moreInfo?.duration == "?m") visibility = View.GONE
        }
        itemView.findViewById<TextView>(R.id.score).apply {
            text = release.anime?.moreInfo?.malscore
            if (release.anime?.moreInfo?.malscore == "?")
                itemView.findViewById<LinearLayout>(R.id.scoreLayout).visibility = View.GONE
        }

        val adultRatings = listOf("R", "R+", "Rx", "18+")

        if (rating != null && rating in adultRatings) {
            itemView.findViewById<CardView>(R.id.ratingCard).visibility = View.VISIBLE
        } else {
            itemView.findViewById<CardView>(R.id.ratingCard).visibility = View.GONE
        }


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

        itemView.findViewById<Button>(R.id.watch_button).setOnClickListener {
            val bundle = Bundle().apply {
                putString("id", animeId)
                putString("title", release.anime?.info?.name)
            }
            val episodesFragment = EpisodesFragment().apply {
                arguments = bundle
            }

            (fragment.requireActivity() as MainActivity).pushFragment(episodesFragment)
        }

        var isFavorite = fragment.prefs.isFavorite(animeId)

        val favorite: ImageButton = itemView.findViewById(R.id.favoriteButton)

        updateFavoriteIcon(isFavorite, favorite, fragment)

        favorite.setOnClickListener {
            isFavorite = if (isFavorite) {
                fragment.prefs.removeFavorite(animeId)
                (fragment.requireActivity() as MainActivity).showSnackbar(itemView.context.getString(R.string.removed_from_favorites))
                false
            } else {
                fragment.prefs.addFavorite(animeId)
                (fragment.requireActivity() as MainActivity).showSnackbar(itemView.context.getString(R.string.added_to_favorites))
                true
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean, favoriteButton: ImageButton, fragment: DetailsFragment) {
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