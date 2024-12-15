package com.kawaidev.kawaime.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment

class GenreAdapter(
    private val fragment: Fragment,
    private var genres: List<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW = 0
        const val GENRE_VIEW = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW -> HeaderViewHolder(inflater.inflate(R.layout.header_item, parent, false))
            GENRE_VIEW -> GenresViewHolder(inflater.inflate(R.layout.small_button_item, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return genres.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_VIEW else GENRE_VIEW
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(fragment.requireContext().getString(R.string.genres))
            is GenresViewHolder -> holder.bind(genres[position - 1])
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val headerText: TextView = view.findViewById(R.id.headerTitle)
        private val headerNote: TextView = view.findViewById(R.id.headerNote)

        fun bind(title: String) {
            headerText.text = title
            headerNote.visibility = View.GONE
        }
    }

    inner class GenresViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.genre)

        fun bind(genre: String) {
            text.text = genre

            itemView.findViewById<MaterialCardView>(R.id.genreCard).setOnClickListener {
                val bundle = Bundle().apply {
                    putString("SEARCH", genre)
                }
                val searchFragment = SearchFragment().apply {
                    arguments = bundle
                }

                (fragment.requireActivity() as MainActivity).pushFragment(searchFragment)
            }
        }
    }
}
