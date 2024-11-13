package com.kawaidev.kawaime.ui.adapters.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.Season
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.LoadImage

class SeasonAdapter(private val fragment: Fragment, private var seasons: List<Season>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SEASON = 1
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_SEASON
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEASON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_season, parent, false)
                SeasonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SeasonViewHolder) {
            holder.bind(seasons[position])
        }
    }

    override fun getItemCount(): Int {
        return seasons.size
    }

    inner class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val title: TextView = itemView.findViewById(R.id.title)
        fun bind(season: Season) {
            LoadImage().loadImage(itemView.context, season.poster, image, blur = 8)
            title.text = season.title

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", season.id)
                }
                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }
        }
    }
}
