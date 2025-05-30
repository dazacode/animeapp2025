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
            itemView.alpha = 0f
            itemView.translationY = 50f
            itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start()
            
            itemView.findViewById<TextView>(R.id.spotlight).text = "✨ Spotlight №${anime.rank}"
            itemView.findViewById<TextView>(R.id.title).text = anime.name
            itemView.findViewById<TextView>(R.id.desc).text = anime.description

            val detailsButton = itemView.findViewById<Button>(R.id.details_button)
            
            detailsButton.setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
            
            detailsButton.setOnClickListener {
                it.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(50)
                    .withEndAction {
                        it.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start()
                    }
                    .start()
                
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