package com.kawaidev.kawaime.ui.adapters.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Screenshot
import com.kawaidev.kawaime.network.routes.AnimeRoutes
import com.kawaidev.kawaime.utils.LoadImage
import com.stfalcon.imageviewer.StfalconImageViewer

class ScreenAdapter(private val fragment: Fragment, private var screenshots: List<Screenshot>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SCREENSHOT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_SCREENSHOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SCREENSHOT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_screenshot, parent, false)
                SeasonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SeasonViewHolder) {
            holder.bind(screenshots[position])
        }
    }

    override fun getItemCount(): Int {
        return screenshots.size
    }

    inner class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)

        fun bind(screenshot: Screenshot) {
            var isImageViewerOpen = false

            val shikimoriUrl = AnimeRoutes.SHIKIMORI + screenshot.original

            LoadImage().loadImage(itemView.context, shikimoriUrl, image)

            image.setOnClickListener {
                if (!isImageViewerOpen) {
                    isImageViewerOpen = true

                    val imageUrls = screenshots.map { AnimeRoutes.SHIKIMORI + it.original }

                    imageUrls.let { urls ->
                        StfalconImageViewer.Builder(itemView.context, urls) { imageView, imageUrl ->
                            itemView.context.let {
                                Glide.with(it)
                                    .load(imageUrl)
                                    .into(imageView)
                            }
                        }
                            .withStartPosition(absoluteAdapterPosition)
                            .withTransitionFrom(image)
                            .allowZooming(true)
                            .withHiddenStatusBar(false)
                            .withDismissListener {
                                isImageViewerOpen = false
                            }
                            .show()
                    }
                }
            }
        }
    }
}
