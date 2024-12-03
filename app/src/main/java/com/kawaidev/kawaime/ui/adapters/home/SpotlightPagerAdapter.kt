package com.kawaidev.kawaime.ui.adapters.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.utils.LoadImage

class SpotlightPagerAdapter(private val fragment: Fragment, private val spotlightData: List<BasicRelease>) : RecyclerView.Adapter<SpotlightPagerAdapter.SpotlightItemViewHolder>() {

    inner class SpotlightItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(anime: BasicRelease) {
            itemView.findViewById<TextView>(R.id.spotlight).text = itemView.context.getString(R.string.spotlight, anime.rank.toString())
            itemView.findViewById<TextView>(R.id.title).text = anime.name
            itemView.findViewById<TextView>(R.id.desc).text = anime.description

            itemView.findViewById<Button>(R.id.details_button).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("id", anime.id)
                }
                val detailsFragment = DetailsFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(detailsFragment)
            }

            LoadImage().loadImage(itemView.context, anime.poster, itemView.findViewById(R.id.image))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotlightItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spotlight_item, parent, false)
        return SpotlightItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpotlightItemViewHolder, position: Int) {
        holder.bind(spotlightData[position])
    }

    override fun getItemCount(): Int = spotlightData.size
}